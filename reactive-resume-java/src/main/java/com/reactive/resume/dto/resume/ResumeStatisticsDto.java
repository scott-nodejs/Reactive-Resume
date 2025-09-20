package com.reactive.resume.dto.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简历统计DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "简历统计信息")
public class ResumeStatisticsDto {

    @Schema(description = "简历ID")
    private String resumeId;

    @Schema(description = "浏览次数")
    private Integer views;

    @Schema(description = "下载次数")
    private Integer downloads;
}
