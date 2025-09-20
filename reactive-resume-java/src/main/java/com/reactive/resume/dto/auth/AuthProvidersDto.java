package com.reactive.resume.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证提供商DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "认证提供商配置")
public class AuthProvidersDto {

    @Schema(description = "是否启用GitHub登录")
    private Boolean github;

    @Schema(description = "是否启用Google登录")
    private Boolean google;

    @Schema(description = "是否启用OpenID登录")
    private Boolean openid;

    @Schema(description = "OpenID提供商名称")
    private String openidName;
}
