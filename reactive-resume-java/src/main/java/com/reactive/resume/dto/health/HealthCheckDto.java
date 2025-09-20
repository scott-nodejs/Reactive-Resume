package com.reactive.resume.dto.health;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康检查结果")
public class HealthCheckDto {

    @Schema(description = "整体状态")
    private String status;

    @Schema(description = "检查时间")
    private LocalDateTime timestamp;

    @Schema(description = "各组件状态")
    private Map<String, ComponentHealthDto> components;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "组件健康状态")
    public static class ComponentHealthDto {

        @Schema(description = "状态")
        private String status;

        @Schema(description = "详细信息")
        private Map<String, Object> details;
    }
}
