package com.reactive.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author Reactive Resume Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
@Schema(description = "用户信息")
public class User extends BaseEntity {

    @Schema(description = "用户ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "用户名", example = "john_doe")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @TableField("username")
    private String username;

    @Schema(description = "邮箱地址", example = "john@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    private String email;

    @Schema(description = "用户姓名", example = "John Doe")
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @TableField("name")
    private String name;

    @Schema(description = "头像URL")
    @TableField("picture")
    private String picture;

    @Schema(description = "密码")
    @JsonIgnore
    @TableField("password")
    private String password;

    @Schema(description = "语言设置", example = "zh-CN")
    @TableField("locale")
    private String locale = "zh-CN";

    @Schema(description = "邮箱是否已验证")
    @TableField("email_verified")
    private Boolean emailVerified = false;

    @Schema(description = "是否启用双因子认证")
    @TableField("two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    @Schema(description = "认证提供商")
    @TableField("provider")
    private String provider = "email";

    @Schema(description = "双因子认证密钥")
    @JsonIgnore
    @TableField("two_factor_secret")
    private String twoFactorSecret;

    @Schema(description = "双因子认证备用码")
    @JsonIgnore
    @TableField("two_factor_backup_codes")
    private String twoFactorBackupCodes;

    @Schema(description = "刷新令牌")
    @JsonIgnore
    @TableField("refresh_token")
    private String refreshToken;

    @Schema(description = "重置密码令牌")
    @JsonIgnore
    @TableField("reset_token")
    private String resetToken;

    @Schema(description = "邮箱验证令牌")
    @JsonIgnore
    @TableField("verification_token")
    private String verificationToken;

    @Schema(description = "最后登录时间")
    @TableField("last_signed_in")
    private LocalDateTime lastSignedIn;
}

