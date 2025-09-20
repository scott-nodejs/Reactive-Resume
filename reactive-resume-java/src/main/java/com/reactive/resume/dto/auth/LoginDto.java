package com.reactive.resume.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Schema(description = "登录请求")
public class LoginDto {

    @Schema(description = "用户名或邮箱", example = "john@example.com")
    @NotBlank(message = "用户名或邮箱不能为空")
    private String identifier;

    @Schema(description = "密码", example = "password123")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "双因子认证码", example = "123456")
    private String code;
}
