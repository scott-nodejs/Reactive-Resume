package com.reactive.resume.security;

import com.reactive.resume.entity.User;
import com.reactive.resume.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户详情服务实现
 * 
 * @author Reactive Resume Team
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getId())
            .password(user.getPassword() != null ? user.getPassword() : "")
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
