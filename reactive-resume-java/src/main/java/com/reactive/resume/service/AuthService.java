package com.reactive.resume.service;

import com.reactive.resume.dto.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证服务接口
 * 
 * @author Reactive Resume Team
 */
public interface AuthService {

    /**
     * 用户注册
     */
    AuthResponseDto register(RegisterDto registerDto, HttpServletResponse response);

    /**
     * 用户登录
     */
    AuthResponseDto login(LoginDto loginDto, HttpServletResponse response);

    /**
     * 用户登出
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 刷新令牌
     */
    AuthResponseDto refresh(HttpServletRequest request, HttpServletResponse response);

    /**
     * 忘记密码
     */
    MessageDto forgotPassword(ForgotPasswordDto forgotPasswordDto);

    /**
     * 重置密码
     */
    MessageDto resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * 修改密码
     */
    MessageDto updatePassword(UpdatePasswordDto updatePasswordDto, HttpServletRequest request);

    /**
     * 获取认证提供商
     */
    AuthProvidersDto getAuthProviders();

    /**
     * 验证邮箱
     */
    MessageDto verifyEmail(String token);

    /**
     * 重新发送验证邮件
     */
    MessageDto resendVerification(HttpServletRequest request);

    /**
     * 启用双因子认证
     */
    TwoFactorSetupDto enableTwoFactor(HttpServletRequest request);

    /**
     * 确认双因子认证
     */
    TwoFactorBackupCodesDto verifyTwoFactor(TwoFactorDto twoFactorDto, HttpServletRequest request);

    /**
     * 禁用双因子认证
     */
    MessageDto disableTwoFactor(TwoFactorDto twoFactorDto, HttpServletRequest request);

    /**
     * 使用备用码登录
     */
    AuthResponseDto twoFactorBackup(TwoFactorBackupDto backupDto, HttpServletRequest request, HttpServletResponse response);
}
