package com.reactive.resume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reactive.resume.common.exception.BusinessException;
import com.reactive.resume.dto.user.UpdateUserDto;
import com.reactive.resume.dto.user.UserDto;
import com.reactive.resume.entity.User;
import com.reactive.resume.mapper.UserMapper;
import com.reactive.resume.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public User findById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public UserDto getCurrentUser(String userId) {
        User user = findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    @Transactional
    public UserDto updateUser(String userId, UpdateUserDto updateUserDto) {
        User user = findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否已被使用
        if (updateUserDto.getUsername() != null && 
            !updateUserDto.getUsername().equals(user.getUsername()) &&
            existsByUsername(updateUserDto.getUsername())) {
            throw new BusinessException("用户名已被使用");
        }

        // 检查邮箱是否已被使用
        if (updateUserDto.getEmail() != null && 
            !updateUserDto.getEmail().equals(user.getEmail()) &&
            existsByEmail(updateUserDto.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }

        // 更新用户信息
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        if (updateUserDto.getPicture() != null) {
            user.setPicture(updateUserDto.getPicture());
        }
        if (updateUserDto.getUsername() != null) {
            user.setUsername(updateUserDto.getUsername());
        }
        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
            user.setEmailVerified(false); // 邮箱变更后需要重新验证
        }
        if (updateUserDto.getLocale() != null) {
            user.setLocale(updateUserDto.getLocale());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        User user = findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 这里可以添加删除用户相关数据的逻辑
        // 比如删除用户的简历、文件等

        userMapper.deleteById(userId);
        log.info("用户已删除: {}", userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectCount(queryWrapper) > 0;
    }
}
