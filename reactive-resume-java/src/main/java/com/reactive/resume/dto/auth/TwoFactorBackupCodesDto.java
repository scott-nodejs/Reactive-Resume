package com.reactive.resume.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 双因子认证备用码DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "双因子认证备用码")
public class TwoFactorBackupCodesDto {

    @Schema(description = "备用验证码列表")
    private List<String> backupCodes;
}
