package com.reactive.resume.service.impl;

import com.reactive.resume.dto.feature.FeatureFlagsDto;
import com.reactive.resume.service.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 功能开关服务实现类
 * 
 * @author Reactive Resume Team
 */
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    @Value("${app.feature.disable-signups:false}")
    private boolean disableSignups;

    @Value("${app.feature.disable-email-auth:false}")
    private boolean disableEmailAuth;

    @Override
    public FeatureFlagsDto getFeatureFlags() {
        return FeatureFlagsDto.builder()
            .isSignupsDisabled(disableSignups)
            .isEmailAuthDisabled(disableEmailAuth)
            .build();
    }

    @Override
    public boolean isSignupsDisabled() {
        return disableSignups;
    }

    @Override
    public boolean isEmailAuthDisabled() {
        return disableEmailAuth;
    }
}
