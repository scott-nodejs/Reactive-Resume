package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.translation.LanguageDto;
import com.reactive.resume.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 翻译控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "国际化翻译", description = "多语言支持接口")
@RestController
@RequestMapping("/translation")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @Operation(summary = "获取支持的语言列表")
    @GetMapping("/languages")
    public Result<List<LanguageDto>> getLanguages() {
        return Result.success(translationService.getSupportedLanguages());
    }
}
