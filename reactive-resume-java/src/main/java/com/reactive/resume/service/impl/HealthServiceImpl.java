package com.reactive.resume.service.impl;

import com.reactive.resume.dto.health.HealthCheckDto;
import com.reactive.resume.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查服务实现类
 *
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private final DataSource dataSource;
//    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public HealthCheckDto getHealthStatus() {
        Map<String, HealthCheckDto.ComponentHealthDto> components = new HashMap<>();

        // 检查数据库
        boolean dbHealthy = isDatabaseHealthy();
        components.put("database", HealthCheckDto.ComponentHealthDto.builder()
            .status(dbHealthy ? "UP" : "DOWN")
            .details(Map.of("type", "MySQL"))
            .build());

        // 检查Redis
        boolean redisHealthy = isRedisHealthy();
        components.put("redis", HealthCheckDto.ComponentHealthDto.builder()
            .status(redisHealthy ? "UP" : "DOWN")
            .details(Map.of("type", "Redis"))
            .build());

        // 检查存储服务
        boolean storageHealthy = isStorageHealthy();
        components.put("storage", HealthCheckDto.ComponentHealthDto.builder()
            .status(storageHealthy ? "UP" : "DOWN")
            .details(Map.of("type", "Minio"))
            .build());

        // 确定整体状态
        String overallStatus = (dbHealthy && redisHealthy && storageHealthy) ? "UP" : "DOWN";

        return HealthCheckDto.builder()
            .status(overallStatus)
            .timestamp(LocalDateTime.now())
            .components(components)
            .build();
    }

    @Override
    public boolean isDatabaseHealthy() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5);
        } catch (Exception e) {
            log.error("数据库健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isRedisHealthy() {
        try {
//            redisTemplate.opsForValue().set("health:check", "ok");
//            String result = (String) redisTemplate.opsForValue().get("health:check");
//            redisTemplate.delete("health:check");
//            return "ok".equals(result);
          return true;
        } catch (Exception e) {
            log.error("Redis健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isStorageHealthy() {
        try {
            // 这里可以添加Minio连接检查
            return true;
        } catch (Exception e) {
            log.error("存储服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}
