package com.reactive.resume.service;

import com.reactive.resume.dto.resume.*;
import com.reactive.resume.entity.Resume;

import java.util.List;

/**
 * 简历服务接口
 * 
 * @author Reactive Resume Team
 */
public interface ResumeService {

    /**
     * 创建简历
     */
    ResumeDto createResume(String userId, CreateResumeDto createResumeDto);

    /**
     * 获取用户的所有简历
     */
    List<ResumeDto> getUserResumes(String userId);

    /**
     * 根据ID获取简历
     */
    ResumeDto getResumeById(String id, String userId);

    /**
     * 根据slug获取简历
     */
    ResumeDto getResumeBySlug(String username, String slug);

    /**
     * 更新简历
     */
    ResumeDto updateResume(String id, String userId, UpdateResumeDto updateResumeDto);

    /**
     * 删除简历
     */
    void deleteResume(String id, String userId);

    /**
     * 复制简历
     */
    ResumeDto duplicateResume(String id, String userId);

    /**
     * 导入简历
     */
    ResumeDto importResume(String userId, ImportResumeDto importResumeDto);

    /**
     * 获取简历统计信息
     */
    ResumeStatisticsDto getResumeStatistics(String id);

    /**
     * 增加浏览次数
     */
    void incrementViews(String id);

    /**
     * 增加下载次数
     */
    void incrementDownloads(String id);

    /**
     * 锁定/解锁简历
     */
    void toggleLock(String id, String userId);
}
