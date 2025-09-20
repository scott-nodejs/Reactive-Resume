package com.reactive.resume.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 更新用户信息DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新用户信息请求")
public class UpdateUserDto {

    @Schema(description = "姓名", example = "John Doe")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;

    @Schema(description = "头像URL")
    private String picture;

    @Schema(description = "用户名", example = "john_doe")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @Schema(description = "邮箱地址", example = "john@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "语言设置", example = "zh-CN")
    private String locale;
}
