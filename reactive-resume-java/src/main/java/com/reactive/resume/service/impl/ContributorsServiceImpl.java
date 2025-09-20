package com.reactive.resume.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.reactive.resume.dto.contributors.ContributorDto;
import com.reactive.resume.service.ContributorsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 贡献者服务实现类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContributorsServiceImpl implements ContributorsService {

    @Value("${app.github.repository:AmruthPillai/Reactive-Resume}")
    private String githubRepository;

    @Value("${app.crowdin.project-id:}")
    private String crowdinProjectId;

    @Value("${app.crowdin.personal-token:}")
    private String crowdinPersonalToken;

    @Override
    public List<ContributorDto> getGitHubContributors() {
        try {
            String url = String.format("https://api.github.com/repos/%s/contributors", githubRepository);
            
            String response = HttpUtil.get(url, 10000);
            JSONArray contributors = JSONUtil.parseArray(response);

            List<ContributorDto> result = new ArrayList<>();
            
            for (int i = 0; i < contributors.size(); i++) {
                JSONObject contributor = contributors.getJSONObject(i);
                
                result.add(ContributorDto.builder()
                    .username(contributor.getStr("login"))
                    .name(contributor.getStr("login"))
                    .avatarUrl(contributor.getStr("avatar_url"))
                    .profileUrl(contributor.getStr("html_url"))
                    .contributions(contributor.getInt("contributions"))
                    .type("github")
                    .build());
            }

            return result;
            
        } catch (Exception e) {
            log.error("获取GitHub贡献者失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<ContributorDto> getCrowdinContributors() {
        try {
            if (cn.hutool.core.util.StrUtil.isBlank(crowdinProjectId) || 
                cn.hutool.core.util.StrUtil.isBlank(crowdinPersonalToken)) {
                return new ArrayList<>();
            }

            String url = String.format("https://api.crowdin.com/api/v2/projects/%s/members", crowdinProjectId);
            
            String response = HttpUtil.createGet(url)
                .header("Authorization", "Bearer " + crowdinPersonalToken)
                .execute()
                .body();

            JSONObject jsonResponse = JSONUtil.parseObj(response);
            JSONArray dataArray = jsonResponse.getJSONArray("data");

            List<ContributorDto> result = new ArrayList<>();
            
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject item = dataArray.getJSONObject(i);
                JSONObject data = item.getJSONObject("data");
                
                result.add(ContributorDto.builder()
                    .username(data.getStr("username"))
                    .name(data.getStr("fullName", data.getStr("username")))
                    .avatarUrl(data.getStr("avatarUrl"))
                    .profileUrl("https://crowdin.com/profile/" + data.getStr("username"))
                    .contributions(0) // Crowdin API 不直接提供贡献数
                    .type("crowdin")
                    .build());
            }

            return result;
            
        } catch (Exception e) {
            log.error("获取Crowdin贡献者失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
