package com.reactive.resume.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息响应DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息响应")
public class MessageDto {

    @Schema(description = "消息内容")
    private String message;
}
