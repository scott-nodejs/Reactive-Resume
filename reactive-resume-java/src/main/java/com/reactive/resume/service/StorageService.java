package com.reactive.resume.service;

import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 文件存储服务接口
 * 
 * @author Reactive Resume Team
 */
public interface StorageService {

    /**
     * 上传图片
     */
    String uploadImage(MultipartFile file, HttpServletRequest request);

    /**
     * 上传文件
     */
    String uploadFile(MultipartFile file, String type, HttpServletRequest request);

    /**
     * 删除文件
     */
    void deleteFile(String type, String filename, HttpServletRequest request);

    /**
     * 上传对象到存储
     */
    String uploadObject(String userId, String type, byte[] data, String filename);

    /**
     * 删除对象
     */
    void deleteObject(String userId, String type, String filename);

    /**
     * 删除用户文件夹
     */
    void deleteUserFolder(String userId);
}
