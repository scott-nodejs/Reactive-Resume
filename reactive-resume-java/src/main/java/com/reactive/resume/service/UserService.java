package com.reactive.resume.service;

import com.reactive.resume.dto.user.UpdateUserDto;
import com.reactive.resume.dto.user.UserDto;
import com.reactive.resume.entity.User;

/**
 * 用户服务接口
 * 
 * @author Reactive Resume Team
 */
public interface UserService {

    /**
     * 根据ID获取用户
     */
    User findById(String id);

    /**
     * 根据用户名获取用户
     */
    User findByUsername(String username);

    /**
     * 根据邮箱获取用户
     */
    User findByEmail(String email);

    /**
     * 获取当前用户信息
     */
    UserDto getCurrentUser(String userId);

    /**
     * 更新用户信息
     */
    UserDto updateUser(String userId, UpdateUserDto updateUserDto);

    /**
     * 删除用户
     */
    void deleteUser(String userId);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}
