package com.reactive.resume.service;

import com.reactive.resume.dto.feature.FeatureFlagsDto;

/**
 * 功能开关服务接口
 * 
 * @author Reactive Resume Team
 */
public interface FeatureService {

    /**
     * 获取功能开关配置
     */
    FeatureFlagsDto getFeatureFlags();

    /**
     * 检查是否禁用注册
     */
    boolean isSignupsDisabled();

    /**
     * 检查是否禁用邮箱认证
     */
    boolean isEmailAuthDisabled();
}
