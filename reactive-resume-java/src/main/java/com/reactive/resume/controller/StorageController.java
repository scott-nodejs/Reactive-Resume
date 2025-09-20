package com.reactive.resume.controller;

import com.reactive.resume.common.Result;
import com.reactive.resume.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 文件存储控制器
 * 
 * @author Reactive Resume Team
 */
@Tag(name = "文件存储", description = "文件上传和管理接口")
@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                    HttpServletRequest request) {
        return Result.success(storageService.uploadImage(file, request));
    }

    @Operation(summary = "上传文件")
    @PostMapping("/file")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file,
                                   @RequestParam("type") String type,
                                   HttpServletRequest request) {
        return Result.success(storageService.uploadFile(file, type, request));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{type}/{filename}")
    public Result<Void> deleteFile(@PathVariable String type,
                                 @PathVariable String filename,
                                 HttpServletRequest request) {
        storageService.deleteFile(type, filename, request);
        return Result.success();
    }
}
