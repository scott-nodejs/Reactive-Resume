package com.reactive.resume.dto.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

/**
 * 更新简历DTO
 * 
 * @author Reactive Resume Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新简历请求")
public class UpdateResumeDto {

    @Schema(description = "简历标题", example = "我的简历")
    @Size(max = 200, message = "简历标题长度不能超过200个字符")
    private String title;

    @Schema(description = "简历别名", example = "my-resume")
    @Size(max = 100, message = "简历别名长度不能超过100个字符")
    private String slug;

    @Schema(description = "简历数据(JSON)")
    private String data;

    @Schema(description = "可见性", example = "private")
    private String visibility;

    @Schema(description = "是否锁定")
    private Boolean locked;
}
