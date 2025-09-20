package com.reactive.resume.dto.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * 导入简历DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "导入简历请求")
public class ImportResumeDto {

    @Schema(description = "简历数据(JSON)")
    @NotBlank(message = "简历数据不能为空")
    private String data;

    @Schema(description = "数据来源", example = "reactive-resume")
    @Builder.Default
    private String source = "reactive-resume";
}
