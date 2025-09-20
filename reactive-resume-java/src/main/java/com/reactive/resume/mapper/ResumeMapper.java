package com.reactive.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactive.resume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 简历Mapper接口
 * 
 * @author Reactive Resume Team
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

    /**
     * 根据用户ID查找所有简历
     */
    @Select("SELECT * FROM resumes WHERE user_id = #{userId} AND deleted = 0 ORDER BY updated_at DESC")
    List<Resume> findByUserId(@Param("userId") String userId);

    /**
     * 根据用户名和别名查找公开简历
     */
    @Select("SELECT r.* FROM resumes r " +
            "JOIN users u ON r.user_id = u.id " +
            "WHERE u.username = #{username} AND r.slug = #{slug} " +
            "AND r.visibility = 'public' AND r.deleted = 0 AND u.deleted = 0 " +
            "LIMIT 1")
    Resume findPublicByUsernameAndSlug(@Param("username") String username, @Param("slug") String slug);

    /**
     * 根据用户ID和简历ID查找简历
     */
    @Select("SELECT * FROM resumes WHERE id = #{id} AND user_id = #{userId} AND deleted = 0 LIMIT 1")
    Resume findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    /**
     * 根据用户ID和别名查找简历
     */
    @Select("SELECT * FROM resumes WHERE user_id = #{userId} AND slug = #{slug} AND deleted = 0 LIMIT 1")
    Resume findByUserIdAndSlug(@Param("userId") String userId, @Param("slug") String slug);
}
