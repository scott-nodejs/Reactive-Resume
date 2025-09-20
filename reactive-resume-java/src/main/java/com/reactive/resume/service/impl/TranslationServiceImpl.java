package com.reactive.resume.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.reactive.resume.dto.translation.LanguageDto;
import com.reactive.resume.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 翻译服务实现类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    @Value("${app.crowdin.project-id:}")
    private String crowdinProjectId;

    @Value("${app.crowdin.personal-token:}")
    private String crowdinPersonalToken;

    @Override
    public List<LanguageDto> getSupportedLanguages() {
        try {
            // 尝试从Crowdin获取
            if (cn.hutool.core.util.StrUtil.isNotBlank(crowdinProjectId) && 
                cn.hutool.core.util.StrUtil.isNotBlank(crowdinPersonalToken)) {
                return fetchLanguagesFromCrowdin();
            }
        } catch (Exception e) {
            log.warn("从Crowdin获取语言列表失败，使用默认列表: {}", e.getMessage());
        }

        // 返回默认语言列表
        return getDefaultLanguages();
    }

    @Override
    public List<LanguageDto> fetchLanguagesFromCrowdin() {
        try {
            String url = String.format("https://api.crowdin.com/api/v2/projects/%s/languages/progress?limit=100", 
                crowdinProjectId);
            
            String response = HttpUtil.createGet(url)
                .header("Authorization", "Bearer " + crowdinPersonalToken)
                .execute()
                .body();

            JSONObject jsonResponse = JSONUtil.parseObj(response);
            JSONArray dataArray = jsonResponse.getJSONArray("data");

            List<LanguageDto> languages = new ArrayList<>();

            // 添加英语
            languages.add(LanguageDto.builder()
                .id("en-US")
                .name("English")
                .progress(100)
                .editorCode("en")
                .locale("en-US")
                .build());

            // 添加其他语言
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject item = dataArray.getJSONObject(i);
                JSONObject data = item.getJSONObject("data");
                JSONObject language = data.getJSONObject("language");
                
                languages.add(LanguageDto.builder()
                    .id(language.getStr("id"))
                    .name(language.getStr("name"))
                    .progress(data.getInt("translationProgress"))
                    .editorCode(language.getStr("editorCode"))
                    .locale(language.getStr("locale"))
                    .build());
            }

            // 按名称排序
            languages.sort((a, b) -> a.getName().compareTo(b.getName()));
            
            return languages;
            
        } catch (Exception e) {
            log.error("从Crowdin获取语言列表失败: {}", e.getMessage());
            throw new RuntimeException("获取语言列表失败");
        }
    }

    /**
     * 获取默认语言列表
     */
    private List<LanguageDto> getDefaultLanguages() {
        return Arrays.asList(
            LanguageDto.builder().id("en-US").name("English").progress(100).editorCode("en").locale("en-US").build(),
            LanguageDto.builder().id("zh-CN").name("简体中文").progress(100).editorCode("zh").locale("zh-CN").build(),
            LanguageDto.builder().id("zh-TW").name("繁體中文").progress(100).editorCode("zh").locale("zh-TW").build(),
            LanguageDto.builder().id("ja-JP").name("日本語").progress(100).editorCode("ja").locale("ja-JP").build(),
            LanguageDto.builder().id("ko-KR").name("한국어").progress(100).editorCode("ko").locale("ko-KR").build(),
            LanguageDto.builder().id("fr-FR").name("Français").progress(100).editorCode("fr").locale("fr-FR").build(),
            LanguageDto.builder().id("de-DE").name("Deutsch").progress(100).editorCode("de").locale("de-DE").build(),
            LanguageDto.builder().id("es-ES").name("Español").progress(100).editorCode("es").locale("es-ES").build(),
            LanguageDto.builder().id("pt-BR").name("Português (Brasil)").progress(100).editorCode("pt").locale("pt-BR").build(),
            LanguageDto.builder().id("ru-RU").name("Русский").progress(100).editorCode("ru").locale("ru-RU").build()
        );
    }
}
