package com.reactive.resume.service;

import com.reactive.resume.dto.contributors.ContributorDto;

import java.util.List;

/**
 * 贡献者服务接口
 * 
 * @author Reactive Resume Team
 */
public interface ContributorsService {

    /**
     * 获取GitHub贡献者
     */
    List<ContributorDto> getGitHubContributors();

    /**
     * 获取Crowdin贡献者
     */
    List<ContributorDto> getCrowdinContributors();
}
