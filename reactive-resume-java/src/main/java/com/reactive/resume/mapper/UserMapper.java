package com.reactive.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reactive.resume.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 * 
 * @author Reactive Resume Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据邮箱或用户名查找用户
     */
    @Select("SELECT * FROM users WHERE (email = #{email} OR username = #{username}) AND deleted = 0 LIMIT 1")
    User findByEmailOrUsername(@Param("email") String email, @Param("username") String username);

    /**
     * 根据重置令牌查找用户
     */
    @Select("SELECT * FROM users WHERE reset_token = #{resetToken} AND deleted = 0 LIMIT 1")
    User findByResetToken(@Param("resetToken") String resetToken);

    /**
     * 根据验证令牌查找用户
     */
    @Select("SELECT * FROM users WHERE verification_token = #{verificationToken} AND deleted = 0 LIMIT 1")
    User findByVerificationToken(@Param("verificationToken") String verificationToken);

    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0 LIMIT 1")
    User findByUsername(@Param("username") String username);
}
