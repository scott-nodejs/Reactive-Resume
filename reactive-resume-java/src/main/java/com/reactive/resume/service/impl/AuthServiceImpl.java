package com.reactive.resume.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.reactive.resume.common.exception.BusinessException;
import com.reactive.resume.dto.auth.*;
import com.reactive.resume.dto.user.UserDto;
import com.reactive.resume.entity.User;
import com.reactive.resume.mapper.UserMapper;
import com.reactive.resume.service.AuthService;
import com.reactive.resume.service.EmailService;
import com.reactive.resume.util.JwtUtil;
import com.reactive.resume.util.TwoFactorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务实现类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final TwoFactorUtil twoFactorUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterDto registerDto, HttpServletResponse response) {
        // 检查用户是否已存在
        User existingUser = userMapper.findByEmailOrUsername(registerDto.getEmail(), registerDto.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户已存在");
        }

        // 创建新用户
        User user = new User();
        user.setId(IdUtil.fastSimpleUUID());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setName(registerDto.getName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setProvider("email");
        user.setEmailVerified(false);
        user.setVerificationToken(IdUtil.fastSimpleUUID());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        // 发送验证邮件
        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        // 生成JWT令牌
        String accessToken = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 更新用户刷新令牌
        user.setRefreshToken(refreshToken);
        userMapper.updateById(user);

        // 设置Cookie
        setTokenCookies(response, accessToken, refreshToken);

        // 构建响应
        AuthResponseDto authResponse = new AuthResponseDto();
        // 注册成功，返回认证响应
        authResponse.setUser(convertToUserDto(user));

        return authResponse;
    }

    @Override
    public AuthResponseDto login(LoginDto loginDto, HttpServletResponse response) {
        // 查找用户
        User user = userMapper.findByEmailOrUsername(loginDto.getIdentifier(), loginDto.getIdentifier());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 检查双因子认证
        if (user.getTwoFactorEnabled()) {
            if (StrUtil.isBlank(loginDto.getCode())) {
                throw new BusinessException("请输入双因子认证码");
            }
            if (!twoFactorUtil.verifyTOTP(user.getTwoFactorSecret(), loginDto.getCode())) {
                throw new BusinessException("双因子认证码错误");
            }
        }

        // 更新最后登录时间
        user.setLastSignedIn(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成JWT令牌
        String accessToken = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 更新用户刷新令牌
        user.setRefreshToken(refreshToken);
        userMapper.updateById(user);

        // 设置Cookie
        setTokenCookies(response, accessToken, refreshToken);

        // 构建响应
        AuthResponseDto authResponse = new AuthResponseDto();
        // 登录成功，返回认证响应
        authResponse.setUser(convertToUserDto(user));

        return authResponse;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 清除Cookie
        clearTokenCookies(response);

        // 清除用户刷新令牌
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (StrUtil.isNotBlank(userId)) {
            User user = userMapper.selectById(userId);
            if (user != null) {
                user.setRefreshToken(null);
                userMapper.updateById(user);
            }
        }
    }

    @Override
    public AuthResponseDto refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromRequest(request);
        if (StrUtil.isBlank(refreshToken)) {
            throw new BusinessException("刷新令牌不存在");
        }

        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userMapper.selectById(userId);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            throw new BusinessException("刷新令牌无效");
        }

        // 生成新的令牌
        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 更新用户刷新令牌
        user.setRefreshToken(newRefreshToken);
        userMapper.updateById(user);

        // 设置Cookie
        setTokenCookies(response, newAccessToken, newRefreshToken);

        // 构建响应
        AuthResponseDto authResponse = new AuthResponseDto();
        authResponse.setMessage("令牌刷新成功");
        authResponse.setUser(convertToUserDto(user));

        return authResponse;
    }

    @Override
    public MessageDto forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userMapper.findByEmailOrUsername(forgotPasswordDto.getEmail(), forgotPasswordDto.getEmail());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成重置令牌
        String resetToken = IdUtil.fastSimpleUUID();
        user.setResetToken(resetToken);
        userMapper.updateById(user);

        // 发送重置邮件
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        MessageDto message = new MessageDto();
        message.setMessage("密码重置邮件已发送");
        return message;
    }

    @Override
    @Transactional
    public MessageDto resetPassword(ResetPasswordDto resetPasswordDto) {
        User user = userMapper.findByResetToken(resetPasswordDto.getToken());
        if (user == null) {
            throw new BusinessException("重置令牌无效");
        }

        // 更新密码并清除重置令牌
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        user.setResetToken(null);
        userMapper.updateById(user);

        MessageDto message = new MessageDto();
        message.setMessage("密码重置成功");
        return message;
    }

    @Override
    public MessageDto updatePassword(UpdatePasswordDto updatePasswordDto, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证当前密码
        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        userMapper.updateById(user);

        MessageDto message = new MessageDto();
        message.setMessage("密码修改成功");
        return message;
    }

    @Override
    public AuthProvidersDto getAuthProviders() {
        AuthProvidersDto providers = new AuthProvidersDto();
        // 邮箱认证始终可用
        providers.setGithub(false);
        providers.setGoogle(false);
        providers.setOpenid(false);
        return providers;
    }

    @Override
    @Transactional
    public MessageDto verifyEmail(String token) {
        User user = userMapper.findByVerificationToken(token);
        if (user == null) {
            throw new BusinessException("验证令牌无效");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userMapper.updateById(user);

        MessageDto message = new MessageDto();
        message.setMessage("邮箱验证成功");
        return message;
    }

    @Override
    public MessageDto resendVerification(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getEmailVerified()) {
            throw new BusinessException("邮箱已验证");
        }

        // 生成新的验证令牌
        String verificationToken = IdUtil.fastSimpleUUID();
        user.setVerificationToken(verificationToken);
        userMapper.updateById(user);

        // 发送验证邮件
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        MessageDto message = new MessageDto();
        message.setMessage("验证邮件已重新发送");
        return message;
    }

    @Override
    public TwoFactorSetupDto enableTwoFactor(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成双因子认证密钥
        String secret = twoFactorUtil.generateSecret();
        String qrCodeUrl = twoFactorUtil.generateQRCodeUrl(secret, user.getEmail(), "Reactive Resume");

        TwoFactorSetupDto setup = new TwoFactorSetupDto();
        setup.setSecret(secret);
        setup.setQrCodeUrl(qrCodeUrl);
        return setup;
    }

    @Override
    @Transactional
    public TwoFactorBackupCodesDto verifyTwoFactor(TwoFactorDto twoFactorDto, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证双因子认证码（使用之前设置的临时secret）
        if (StrUtil.isBlank(user.getTwoFactorSecret())) {
            throw new BusinessException("请先设置双因子认证");
        }
        
        if (!twoFactorUtil.verifyTOTP(user.getTwoFactorSecret(), twoFactorDto.getCode())) {
            throw new BusinessException("双因子认证码错误");
        }

        // 启用双因子认证
        user.setTwoFactorEnabled(true);
        
        List<String> backupCodes = twoFactorUtil.generateBackupCodes();
        user.setTwoFactorBackupCodes(String.join(",", backupCodes));
        
        userMapper.updateById(user);

        TwoFactorBackupCodesDto backupCodesDto = new TwoFactorBackupCodesDto();
        backupCodesDto.setBackupCodes(backupCodes);
        return backupCodesDto;
    }

    @Override
    @Transactional
    public MessageDto disableTwoFactor(TwoFactorDto twoFactorDto, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证双因子认证码
        if (!twoFactorUtil.verifyTOTP(user.getTwoFactorSecret(), twoFactorDto.getCode())) {
            throw new BusinessException("双因子认证码错误");
        }

        // 禁用双因子认证
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        user.setTwoFactorBackupCodes(null);
        userMapper.updateById(user);

        MessageDto message = new MessageDto();
        message.setMessage("双因子认证已禁用");
        return message;
    }

    @Override
    public AuthResponseDto twoFactorBackup(TwoFactorBackupDto backupDto, HttpServletRequest request, HttpServletResponse response) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证备用码
        String[] backupCodes = user.getTwoFactorBackupCodes().split(",");
        boolean codeValid = false;
        for (String code : backupCodes) {
            if (code.equals(backupDto.getBackupCode())) {
                codeValid = true;
                break;
            }
        }

        if (!codeValid) {
            throw new BusinessException("备用码无效");
        }

        // 更新最后登录时间
        user.setLastSignedIn(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成JWT令牌
        String accessToken = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 更新用户刷新令牌
        user.setRefreshToken(refreshToken);
        userMapper.updateById(user);

        // 设置Cookie
        setTokenCookies(response, accessToken, refreshToken);

        // 构建响应
        AuthResponseDto authResponse = new AuthResponseDto();
        // 登录成功，返回认证响应
        authResponse.setUser(convertToUserDto(user));

        return authResponse;
    }

    /**
     * 设置令牌Cookie
     */
    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(24 * 60 * 60); // 24小时
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7天
        response.addCookie(refreshCookie);
    }

    /**
     * 清除令牌Cookie
     */
    private void clearTokenCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", "");
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    /**
     * 从请求中获取刷新令牌
     */
    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 转换为UserDto
     */
    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }
}
