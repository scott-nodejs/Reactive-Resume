package com.reactive.resume.service;

import com.reactive.resume.dto.resume.ResumeDto;
import jakarta.servlet.http.HttpServletResponse;

/**
 * PDF生成服务接口
 * 
 * @author Reactive Resume Team
 */
public interface PrinterService {

    /**
     * 生成简历PDF
     */
    String generateResumePdf(ResumeDto resume);

    /**
     * 生成简历预览图
     */
    String generateResumePreview(ResumeDto resume);

    /**
     * 直接输出PDF到响应
     */
    void printResumeToResponse(ResumeDto resume, HttpServletResponse response);

    /**
     * 检查浏览器服务是否可用
     */
    boolean isBrowserAvailable();
}
