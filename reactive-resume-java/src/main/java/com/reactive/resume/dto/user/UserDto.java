package com.reactive.resume.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserDto {

    @Schema(description = "用户ID")
    private String id;

    @Schema(description = "用户名")
    private String name;

    @Schema(description = "头像URL")
    private String picture;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "语言设置")
    private String locale;

    @Schema(description = "邮箱是否已验证")
    private Boolean emailVerified;

    @Schema(description = "是否启用双因子认证")
    private Boolean twoFactorEnabled;

    @Schema(description = "登录提供商")
    private String provider;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
