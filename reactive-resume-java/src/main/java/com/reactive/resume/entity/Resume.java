package com.reactive.resume.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * 简历实体类
 *
 * @author Reactive Resume Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "resumes", autoResultMap = true)
@Schema(description = "简历信息")
public class Resume extends BaseEntity {

    @Schema(description = "简历ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "简历标题", example = "我的简历")
    @NotBlank(message = "简历标题不能为空")
    @Size(max = 200, message = "简历标题长度不能超过200个字符")
    @TableField("title")
    private String title;

    @Schema(description = "简历别名", example = "my-resume")
    @NotBlank(message = "简历别名不能为空")
    @Size(max = 100, message = "简历别名长度不能超过100个字符")
    @TableField("slug")
    private String slug;

    @Schema(description = "简历数据")
    @TableField(value = "data", typeHandler = JacksonTypeHandler.class)
    private String data;

    @Schema(description = "可见性", example = "private")
    @TableField("visibility")
    private String visibility = "private";

    @Schema(description = "是否锁定")
    @TableField("locked")
    private Boolean locked = false;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private String userId;

    @Schema(description = "浏览次数")
    @TableField(exist = false)
    private Integer views = 0;

    @Schema(description = "下载次数")
    @TableField(exist = false)
    private Integer downloads = 0;
}

