package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.contributors.ContributorDto;
import com.reactive.resume.service.ContributorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 贡献者控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "贡献者", description = "项目贡献者信息接口")
@RestController
@RequestMapping("/contributors")
@RequiredArgsConstructor
public class ContributorsController {

    private final ContributorsService contributorsService;

    @Operation(summary = "获取GitHub贡献者")
    @GetMapping("/github")
    public List<ContributorDto> getGitHubContributors() {
        return contributorsService.getGitHubContributors();
    }

    @Operation(summary = "获取Crowdin贡献者")
    @GetMapping("/crowdin")
    public List<ContributorDto> getCrowdinContributors() {
        return contributorsService.getCrowdinContributors();
    }
}
