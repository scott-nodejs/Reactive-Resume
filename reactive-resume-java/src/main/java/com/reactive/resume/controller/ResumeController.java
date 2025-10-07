package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.dto.resume.*;
import com.reactive.resume.service.ResumeService;
import com.reactive.resume.service.PrinterService;
import com.reactive.resume.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 简历控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "简历管理", description = "简历相关接口")
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@Validated
public class ResumeController {

    private final ResumeService resumeService;
    private final PrinterService printerService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "创建简历")
    @PostMapping
    public Result<ResumeDto> createResume(@Valid @RequestBody CreateResumeDto createResumeDto,
                                        HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        // 开发环境：如果无法获取用户ID，使用默认用户ID
        if (userId == null) {
            userId = "demo-user-id"; // 确保数据库中存在这个用户
        }
        return Result.success(resumeService.createResume(userId, createResumeDto));
    }

    @Operation(summary = "导入简历")
    @PostMapping("/import")
    public Result<ResumeDto> importResume(@Valid @RequestBody ImportResumeDto importResumeDto,
                                        HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        return Result.success(resumeService.importResume(userId, importResumeDto));
    }

    @Operation(summary = "获取用户所有简历")
    @GetMapping
    public Result<List<ResumeDto>> getAllResumes(HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        return Result.success(resumeService.getUserResumes(userId));
    }

    @Operation(summary = "根据ID获取简历")
    @GetMapping("/{id}")
    public Result<ResumeDto> getResumeById(@PathVariable String id, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        return Result.success(resumeService.getResumeById(id, userId));
    }

    @Operation(summary = "根据用户名和别名获取公开简历")
    @GetMapping("/public/{username}/{slug}")
    public Result<ResumeDto> getPublicResume(@PathVariable String username, 
                                           @PathVariable String slug,
                                           HttpServletRequest request) {
        return Result.success(resumeService.getResumeBySlug(username, slug));
    }

    @Operation(summary = "获取简历统计信息")
    @GetMapping("/{id}/statistics")
    public Result<ResumeStatisticsDto> getResumeStatistics(@PathVariable String id) {
        return Result.success(resumeService.getResumeStatistics(id));
    }

    @Operation(summary = "更新简历")
    @PatchMapping("/{id}")
    public Result<ResumeDto> updateResume(@PathVariable String id,
                                        @Valid @RequestBody UpdateResumeDto updateResumeDto,
                                        HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        return Result.success(resumeService.updateResume(id, userId, updateResumeDto));
    }

    @Operation(summary = "锁定/解锁简历")
    @PatchMapping("/{id}/lock")
    public Result<Void> lockResume(@PathVariable String id,
                                 @RequestParam Boolean locked,
                                 HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        resumeService.toggleLock(id, userId);
        return Result.success();
    }

    @Operation(summary = "删除简历")
    @DeleteMapping("/{id}")
    public Result<Void> deleteResume(@PathVariable String id, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        resumeService.deleteResume(id, userId);
        return Result.success();
    }

    @Operation(summary = "复制简历")
    @PostMapping("/{id}/duplicate")
    public Result<ResumeDto> duplicateResume(@PathVariable String id, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        return Result.success(resumeService.duplicateResume(id, userId));
    }

    @Operation(summary = "打印简历为PDF")
    @GetMapping("/print/{id}")
    public void printResume(@PathVariable String id, 
                           HttpServletRequest request, 
                           HttpServletResponse response) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        
        ResumeDto resume = resumeService.getResumeById(id, userId);
        printerService.printResumeToResponse(resume, response);
    }

    @Operation(summary = "生成简历PDF文件")
    @PostMapping("/{id}/pdf")
    public Result<String> generateResumePdf(@PathVariable String id, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        
        ResumeDto resume = resumeService.getResumeById(id, userId);
        String pdfUrl = printerService.generateResumePdf(resume);
        return Result.success(pdfUrl);
    }

    @Operation(summary = "生成简历预览图")
    @PostMapping("/{id}/preview")
    public Result<String> generateResumePreview(@PathVariable String id, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            userId = "demo-user-id";
        }
        
        ResumeDto resume = resumeService.getResumeById(id, userId);
        String previewUrl = printerService.generateResumePreview(resume);
        return Result.success(previewUrl);
    }

    // 上传简历文件功能暂未实现
    // @Operation(summary = "上传简历文件")
    // @PostMapping("/upload")
    // public Result<ResumeDto> uploadResume(@RequestParam("file") MultipartFile file,
    //                                     HttpServletRequest request) {
    //     String userId = jwtUtil.getUserIdFromRequest(request);
    //     return Result.success(resumeService.uploadResume(file, userId));
    // }
}
