package com.reactive.resume.dto.translation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 语言DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "语言信息")
public class LanguageDto {

    @Schema(description = "语言ID")
    private String id;

    @Schema(description = "语言名称")
    private String name;

    @Schema(description = "翻译进度")
    private Integer progress;

    @Schema(description = "编辑器代码")
    private String editorCode;

    @Schema(description = "区域设置")
    private String locale;
}
