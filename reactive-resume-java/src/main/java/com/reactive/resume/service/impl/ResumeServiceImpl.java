package com.reactive.resume.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reactive.resume.common.exception.BusinessException;
import com.reactive.resume.dto.resume.*;
import com.reactive.resume.entity.Resume;
import com.reactive.resume.entity.User;
import com.reactive.resume.mapper.ResumeMapper;
import com.reactive.resume.service.ResumeService;
import com.reactive.resume.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.OgnlContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简历服务实现类
 *
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ResumeDto createResume(String userId, CreateResumeDto createResumeDto) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成slug
        String slug = generateSlug(createResumeDto.getSlug(), createResumeDto.getTitle());

        // 检查slug是否已存在
        if (existsByUserIdAndSlug(userId, slug)) {
            throw new BusinessException("简历别名已存在");
        }

        Resume resume = new Resume();
        resume.setId(IdUtil.fastSimpleUUID());
        resume.setTitle(createResumeDto.getTitle());
        resume.setSlug(slug);
        resume.setData(""); // 默认空JSON
        resume.setVisibility(createResumeDto.getVisibility());
        resume.setLocked(false);
        resume.setUserId(userId);
        resume.setViews(0);
        resume.setDownloads(0);
        resume.setCreatedAt(LocalDateTime.now());
        resume.setUpdatedAt(LocalDateTime.now());

        resumeMapper.insert(resume);

        return convertToDto(resume);
    }

    @Override
    public List<ResumeDto> getUserResumes(String userId) {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("updated_at");

        List<Resume> resumes = resumeMapper.selectList(queryWrapper);
        return resumes.stream()
                     .map(this::convertToDto)
                     .collect(Collectors.toList());
    }

    @Override
    public ResumeDto getResumeById(String id, String userId) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        // 检查权限
        if (!resume.getUserId().equals(userId) && !"public".equals(resume.getVisibility())) {
            throw new BusinessException("无权访问此简历");
        }

        return convertToDto(resume);
    }

    @Override
    public ResumeDto getResumeBySlug(String username, String slug) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId())
                   .eq("slug", slug)
                   .eq("visibility", "public");

        Resume resume = resumeMapper.selectOne(queryWrapper);
        if (resume == null) {
            throw new BusinessException("简历不存在或不公开");
        }

        // 增加浏览次数
        incrementViews(resume.getId());

        return convertToDto(resume);
    }

    @Override
    @Transactional
    public ResumeDto updateResume(String id, String userId, UpdateResumeDto updateResumeDto) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此简历");
        }

        if (resume.getLocked()) {
            throw new BusinessException("简历已锁定，无法修改");
        }

        // 更新字段
        if (updateResumeDto.getTitle() != null) {
            resume.setTitle(updateResumeDto.getTitle());
        }
        if (updateResumeDto.getSlug() != null) {
            String newSlug = generateSlug(updateResumeDto.getSlug(), resume.getTitle());
            if (!newSlug.equals(resume.getSlug()) && existsByUserIdAndSlug(userId, newSlug)) {
                throw new BusinessException("简历别名已存在");
            }
            resume.setSlug(newSlug);
        }
        if (updateResumeDto.getData() != null) {
            resume.setData(updateResumeDto.getData());
        }
        if (updateResumeDto.getVisibility() != null) {
            resume.setVisibility(updateResumeDto.getVisibility());
        }
        if (updateResumeDto.getLocked() != null) {
            resume.setLocked(updateResumeDto.getLocked());
        }

        resume.setUpdatedAt(LocalDateTime.now());
        resumeMapper.updateById(resume);

        return convertToDto(resume);
    }

    @Override
    @Transactional
    public void deleteResume(String id, String userId) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此简历");
        }

        resumeMapper.deleteById(id);
        log.info("简历已删除: {}", id);
    }

    @Override
    @Transactional
    public ResumeDto duplicateResume(String id, String userId) {
        Resume originalResume = resumeMapper.selectById(id);
        if (originalResume == null) {
            throw new BusinessException("简历不存在");
        }

        if (!originalResume.getUserId().equals(userId)) {
            throw new BusinessException("无权复制此简历");
        }

        // 创建副本
        Resume duplicatedResume = new Resume();
        BeanUtils.copyProperties(originalResume, duplicatedResume);
        duplicatedResume.setId(IdUtil.fastSimpleUUID());
        duplicatedResume.setTitle(originalResume.getTitle() + " (副本)");
        duplicatedResume.setSlug(generateUniqueSlug(userId, originalResume.getSlug() + "-copy"));
        duplicatedResume.setViews(0);
        duplicatedResume.setDownloads(0);
        duplicatedResume.setCreatedAt(LocalDateTime.now());
        duplicatedResume.setUpdatedAt(LocalDateTime.now());

        resumeMapper.insert(duplicatedResume);

        return convertToDto(duplicatedResume);
    }

    @Override
    @Transactional
    public ResumeDto importResume(String userId, ImportResumeDto importResumeDto) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        Resume resume = new Resume();
        resume.setId(IdUtil.fastSimpleUUID());
        resume.setTitle("导入的简历");
        resume.setSlug(generateUniqueSlug(userId, "imported-resume"));
        resume.setData(importResumeDto.getData());
        resume.setVisibility("private");
        resume.setLocked(false);
        resume.setUserId(userId);
        resume.setViews(0);
        resume.setDownloads(0);
        resume.setCreatedAt(LocalDateTime.now());
        resume.setUpdatedAt(LocalDateTime.now());

        resumeMapper.insert(resume);

        return convertToDto(resume);
    }

    @Override
    public ResumeStatisticsDto getResumeStatistics(String id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        return ResumeStatisticsDto.builder()
                .resumeId(id)
                .views(resume.getViews())
                .downloads(resume.getDownloads())
                .build();
    }

    @Override
    @Transactional
    public void incrementViews(String id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume != null) {
            resume.setViews(resume.getViews() + 1);
            resumeMapper.updateById(resume);
        }
    }

    @Override
    @Transactional
    public void incrementDownloads(String id) {
        Resume resume = resumeMapper.selectById(id);
        if (resume != null) {
            resume.setDownloads(resume.getDownloads() + 1);
            resumeMapper.updateById(resume);
        }
    }

    @Override
    @Transactional
    public void toggleLock(String id, String userId) {
        Resume resume = resumeMapper.selectById(id);
        if (resume == null) {
            throw new BusinessException("简历不存在");
        }

        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此简历");
        }

        resume.setLocked(!resume.getLocked());
        resume.setUpdatedAt(LocalDateTime.now());
        resumeMapper.updateById(resume);
    }

    /**
     * 转换为DTO
     */
    private ResumeDto convertToDto(Resume resume) {
        ResumeDto dto = new ResumeDto();
        BeanUtils.copyProperties(resume, dto);
        return dto;
    }

    /**
     * 生成slug
     */
    private String generateSlug(String slug, String title) {
        if (StrUtil.isNotBlank(slug)) {
            return slug.toLowerCase().replaceAll("[^a-z0-9-]", "-");
        }
        return title.toLowerCase().replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", "-");
    }

    /**
     * 生成唯一slug
     */
    private String generateUniqueSlug(String userId, String baseSlug) {
        String slug = generateSlug(baseSlug, baseSlug);
        int counter = 1;

        while (existsByUserIdAndSlug(userId, slug)) {
            slug = generateSlug(baseSlug + "-" + counter, baseSlug + "-" + counter);
            counter++;
        }

        return slug;
    }

    /**
     * 检查slug是否存在
     */
    private boolean existsByUserIdAndSlug(String userId, String slug) {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("slug", slug);
        return resumeMapper.selectCount(queryWrapper) > 0;
    }
}
