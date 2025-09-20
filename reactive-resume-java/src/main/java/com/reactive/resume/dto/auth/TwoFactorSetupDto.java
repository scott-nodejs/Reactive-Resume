package com.reactive.resume.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 双因子认证设置DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "双因子认证设置")
public class TwoFactorSetupDto {

    @Schema(description = "密钥")
    private String secret;

    @Schema(description = "QR码URL")
    private String qrCodeUrl;

    @Schema(description = "备用验证码")
    private List<String> backupCodes;
}
