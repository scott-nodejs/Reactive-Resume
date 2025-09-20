package com.reactive.resume.service;

import com.reactive.resume.dto.health.HealthCheckDto;

/**
 * 健康检查服务接口
 * 
 * @author Reactive Resume Team
 */
public interface HealthService {

    /**
     * 获取系统健康状态
     */
    HealthCheckDto getHealthStatus();

    /**
     * 检查数据库连接
     */
    boolean isDatabaseHealthy();

    /**
     * 检查Redis连接
     */
    boolean isRedisHealthy();

    /**
     * 检查存储服务
     */
    boolean isStorageHealthy();
}
