package com.reactive.resume.dto.feature;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能开关DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "功能开关配置")
public class FeatureFlagsDto {

    @Schema(description = "是否禁用用户注册")
    private Boolean isSignupsDisabled;

    @Schema(description = "是否禁用邮箱认证")
    private Boolean isEmailAuthDisabled;
}
