package com.reactive.resume.dto.resume;

import com.reactive.resume.dto.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 简历DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "简历信息")
public class ResumeDto {

    @Schema(description = "简历ID")
    private String id;

    @Schema(description = "简历标题")
    private String title;

    @Schema(description = "简历别名")
    private String slug;

    @Schema(description = "简历数据(JSON)")
    private String data;

    @Schema(description = "可见性")
    private String visibility;

    @Schema(description = "是否锁定")
    private Boolean locked;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户信息")
    private UserDto user;

    @Schema(description = "浏览次数")
    private Integer views;

    @Schema(description = "下载次数")
    private Integer downloads;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
