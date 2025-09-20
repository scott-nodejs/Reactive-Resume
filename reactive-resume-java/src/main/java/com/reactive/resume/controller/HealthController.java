package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.health.HealthCheckDto;
import com.reactive.resume.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "健康检查", description = "系统健康状态检查接口")
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @Operation(summary = "系统健康检查")
    @GetMapping
    public Result<HealthCheckDto> healthCheck() {
        return Result.success(healthService.getHealthStatus());
    }

    @Operation(summary = "获取环境信息")
    @GetMapping("/environment")
    public Result<Map<String, Object>> getEnvironment() {
        // 简单返回一些基本环境信息
        Map<String, Object> env = new HashMap<>();
        env.put("java.version", System.getProperty("java.version"));
        env.put("os.name", System.getProperty("os.name"));
        env.put("active.profiles", "default");
        return Result.success(env);
    }
}
