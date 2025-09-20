package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.auth.*;
import com.reactive.resume.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * 认证控制器
 *
 * @author Reactive Resume Team
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto,
                                          HttpServletResponse response) {
        return Result.success(authService.register(registerDto, response));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto,
                                       HttpServletResponse response) {
        return Result.success(authService.login(loginDto, response));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return Result.success();
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public Result<AuthResponseDto> refresh(HttpServletRequest request, HttpServletResponse response) {
        return Result.success(authService.refresh(request, response));
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgot-password")
    public Result<MessageDto> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        return Result.success(authService.forgotPassword(forgotPasswordDto));
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<MessageDto> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        return Result.success(authService.resetPassword(resetPasswordDto));
    }

    @Operation(summary = "修改密码")
    @PatchMapping("/password")
    public Result<MessageDto> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto,
                                           HttpServletRequest request) {
        return Result.success(authService.updatePassword(updatePasswordDto, request));
    }

    @Operation(summary = "获取认证提供商")
    @GetMapping("/providers")
    public Result<AuthProvidersDto> getAuthProviders() {
        return Result.success(authService.getAuthProviders());
    }

    @Operation(summary = "验证邮箱")
    @PostMapping("/verify-email")
    public Result<MessageDto> verifyEmail(@RequestParam String token) {
        return Result.success(authService.verifyEmail(token));
    }

    @Operation(summary = "重新发送验证邮件")
    @PostMapping("/resend-verification")
    public Result<MessageDto> resendVerification(HttpServletRequest request) {
        return Result.success(authService.resendVerification(request));
    }

    @Operation(summary = "启用双因子认证")
    @PostMapping("/two-factor/enable")
    public Result<TwoFactorSetupDto> enableTwoFactor(HttpServletRequest request) {
        return Result.success(authService.enableTwoFactor(request));
    }

    @Operation(summary = "确认双因子认证")
    @PostMapping("/two-factor/verify")
    public Result<TwoFactorBackupCodesDto> verifyTwoFactor(@Valid @RequestBody TwoFactorDto twoFactorDto,
                                                         HttpServletRequest request) {
        return Result.success(authService.verifyTwoFactor(twoFactorDto, request));
    }

    @Operation(summary = "禁用双因子认证")
    @PostMapping("/two-factor/disable")
    public Result<MessageDto> disableTwoFactor(@Valid @RequestBody TwoFactorDto twoFactorDto,
                                             HttpServletRequest request) {
        return Result.success(authService.disableTwoFactor(twoFactorDto, request));
    }

    @Operation(summary = "使用备用码登录")
    @PostMapping("/two-factor/backup")
    public Result<AuthResponseDto> twoFactorBackup(@Valid @RequestBody TwoFactorBackupDto backupDto,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        return Result.success(authService.twoFactorBackup(backupDto, request, response));
    }
}

