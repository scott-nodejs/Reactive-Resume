package com.reactive.resume.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * 双因子认证备用码验证DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "双因子认证备用码验证")
public class TwoFactorBackupDto {

    @Schema(description = "备用验证码", example = "12345678")
    @NotBlank(message = "备用验证码不能为空")
    private String backupCode;
}
