package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.feature.FeatureFlagsDto;
import com.reactive.resume.service.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能开关控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "功能开关", description = "系统功能开关配置接口")
@RestController
@RequestMapping("/feature")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @Operation(summary = "获取功能开关配置")
    @GetMapping("/flags")
    public Result<FeatureFlagsDto> getFeatureFlags() {
        return Result.success(featureService.getFeatureFlags());
    }
}
