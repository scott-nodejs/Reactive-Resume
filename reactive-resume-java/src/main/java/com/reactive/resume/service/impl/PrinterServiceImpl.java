package com.reactive.resume.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.reactive.resume.common.exception.BusinessException;
import com.reactive.resume.dto.resume.ResumeDto;
import com.reactive.resume.service.PrinterService;
import com.reactive.resume.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * PDF生成服务实现类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrinterServiceImpl implements PrinterService {

    private final StorageService storageService;

    @Value("${app.chrome.url:}")
    private String chromeUrl;

    @Value("${app.chrome.token:}")
    private String chromeToken;

    @Value("${app.artboard.url:http://localhost:6173}")
    private String artboardUrl;

    @Override
    public String generateResumePdf(ResumeDto resume) {
        if (!isBrowserAvailable()) {
            throw new BusinessException("PDF生成服务不可用");
        }

        try {
            // 构建简历预览URL
            String previewUrl = String.format("%s/artboard/preview?resumeId=%s", artboardUrl, resume.getId());
            
            // 调用Chrome服务生成PDF
            byte[] pdfData = generatePdfFromUrl(previewUrl);
            
            // 上传到存储服务
            return storageService.uploadObject(resume.getUserId(), "resumes", pdfData, resume.getTitle());
            
        } catch (Exception e) {
            log.error("生成简历PDF失败: {}", e.getMessage());
            throw new BusinessException("PDF生成失败: " + e.getMessage());
        }
    }

    @Override
    public String generateResumePreview(ResumeDto resume) {
        if (!isBrowserAvailable()) {
            throw new BusinessException("预览生成服务不可用");
        }

        try {
            // 构建简历预览URL
            String previewUrl = String.format("%s/artboard/preview?resumeId=%s", artboardUrl, resume.getId());
            
            // 调用Chrome服务生成截图
            byte[] imageData = generateScreenshotFromUrl(previewUrl);
            
            // 上传到存储服务
            return storageService.uploadObject(resume.getUserId(), "previews", imageData, resume.getTitle());
            
        } catch (Exception e) {
            log.error("生成简历预览失败: {}", e.getMessage());
            throw new BusinessException("预览生成失败: " + e.getMessage());
        }
    }

    @Override
    public void printResumeToResponse(ResumeDto resume, HttpServletResponse response) {
        if (!isBrowserAvailable()) {
            throw new BusinessException("PDF生成服务不可用，请确保Chrome服务已启动在 " + chromeUrl);
        }

        try {
            // 构建简历预览URL
            String previewUrl = String.format("%s/artboard/preview?resumeId=%s", artboardUrl, resume.getId());
            
            // 调用Chrome服务生成PDF
            byte[] pdfData = generatePdfFromUrl(previewUrl);
            
            // 设置响应头
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                String.format("attachment; filename=\"%s.pdf\"", resume.getTitle()));
            response.setContentLength(pdfData.length);
            
            // 写入响应
            response.getOutputStream().write(pdfData);
            response.getOutputStream().flush();
            
        } catch (Exception e) {
            log.error("输出简历PDF失败: {}", e.getMessage());
            throw new BusinessException("PDF输出失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isBrowserAvailable() {
        if (cn.hutool.core.util.StrUtil.isBlank(chromeUrl)) {
            return false;
        }

        try {
            // 检查Chrome服务健康状态
            String healthUrl = chromeUrl + "/health";
            String response = HttpUtil.get(healthUrl, 5000);
            return response != null && response.contains("ok");
        } catch (Exception e) {
            log.warn("Chrome服务不可用: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从URL生成PDF
     */
    private byte[] generatePdfFromUrl(String url) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("url", url);
            params.put("format", "A4");
            params.put("printBackground", true);
            params.put("margin", Map.of(
                "top", "0.5in",
                "right", "0.5in",
                "bottom", "0.5in",
                "left", "0.5in"
            ));

            String requestBody = JSONUtil.toJsonStr(params);
            
            // 调用Chrome服务API
            String apiUrl = chromeUrl + "/pdf";
            if (cn.hutool.core.util.StrUtil.isNotBlank(chromeToken)) {
                apiUrl += "?token=" + chromeToken;
            }
            
            return HttpUtil.createPost(apiUrl)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .execute()
                .bodyBytes();
                
        } catch (Exception e) {
            log.error("调用Chrome服务生成PDF失败: {}", e.getMessage());
            throw new BusinessException("PDF生成失败");
        }
    }

    /**
     * 从URL生成截图
     */
    private byte[] generateScreenshotFromUrl(String url) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("url", url);
            params.put("type", "jpeg");
            params.put("quality", 80);
            params.put("fullPage", false);
            params.put("clip", Map.of(
                "x", 0,
                "y", 0,
                "width", 800,
                "height", 600
            ));

            String requestBody = JSONUtil.toJsonStr(params);
            
            // 调用Chrome服务API
            String apiUrl = chromeUrl + "/screenshot";
            if (cn.hutool.core.util.StrUtil.isNotBlank(chromeToken)) {
                apiUrl += "?token=" + chromeToken;
            }
            
            return HttpUtil.createPost(apiUrl)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .execute()
                .bodyBytes();
                
        } catch (Exception e) {
            log.error("调用Chrome服务生成截图失败: {}", e.getMessage());
            throw new BusinessException("截图生成失败");
        }
    }
}
