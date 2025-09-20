package com.reactive.resume.dto.contributors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 贡献者DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "贡献者信息")
public class ContributorDto {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "显示名称")
    private String name;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "个人主页URL")
    private String profileUrl;

    @Schema(description = "贡献次数")
    private Integer contributions;

    @Schema(description = "贡献类型")
    private String type;
}
