package com.reactive.resume.service;

import com.reactive.resume.dto.translation.LanguageDto;

import java.util.List;

/**
 * 翻译服务接口
 * 
 * @author Reactive Resume Team
 */
public interface TranslationService {

    /**
     * 获取支持的语言列表
     */
    List<LanguageDto> getSupportedLanguages();

    /**
     * 从Crowdin获取语言列表
     */
    List<LanguageDto> fetchLanguagesFromCrowdin();
}
