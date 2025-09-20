package com.reactive.resume.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.reactive.resume.common.exception.BusinessException;
import com.reactive.resume.service.StorageService;
import com.reactive.resume.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * 文件存储服务实现类
 *
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

//    private final MinioClient minioClient;
    private final JwtUtil jwtUtil;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @PostConstruct
    public void init() {
//        try {
//            // 检查存储桶是否存在，不存在则创建
//            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//            if (!exists) {
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//                log.info("创建存储桶: {}", bucketName);
//            }
//        } catch (Exception e) {
//            log.error("初始化存储服务失败: {}", e.getMessage());
//        }
    }

    @Override
    public String uploadImage(MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }

        String userId = jwtUtil.getUserIdFromRequest(request);
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }

        try {
            // 压缩图片
            byte[] compressedImage = compressImage(file.getBytes(), 600, 600, 0.8f);
            String filename = IdUtil.fastSimpleUUID();

            return uploadObject(userId, "pictures", compressedImage, filename);
        } catch (IOException e) {
            log.error("图片处理失败: {}", e.getMessage());
            throw new BusinessException("图片处理失败");
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String type, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String userId = jwtUtil.getUserIdFromRequest(request);
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }

        try {
            String filename = file.getOriginalFilename();
            if (StrUtil.isBlank(filename)) {
                filename = IdUtil.fastSimpleUUID();
            }

            return uploadObject(userId, type, file.getBytes(), filename);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public void deleteFile(String type, String filename, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        if (StrUtil.isBlank(userId)) {
            throw new BusinessException("用户未登录");
        }

        deleteObject(userId, type, filename);
    }

    @Override
    public String uploadObject(String userId, String type, byte[] data, String filename) {
        try {
            // 标准化文件名
            String normalizedFilename = normalizeFilename(filename);
            String extension = getExtensionByType(type);
            String objectName = String.format("%s/%s/%s.%s", userId, type, normalizedFilename, extension);

            // 设置内容类型
            String contentType = getContentTypeByType(type);

            // 上传文件
//            minioClient.putObject(
//                PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(objectName)
//                    .stream(new ByteArrayInputStream(data), data.length, -1)
//                    .contentType(contentType)
//                    .build()
//            );

            // 返回访问URL
            return String.format("%s/%s/%s", endpoint, bucketName, objectName);

        } catch (Exception e) {
            log.error("上传对象失败: {}", e.getMessage());
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public void deleteObject(String userId, String type, String filename) {
        try {
            String extension = getExtensionByType(type);
            String objectName = String.format("%s/%s/%s.%s", userId, type, filename, extension);

//            minioClient.removeObject(
//                RemoveObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(objectName)
//                    .build()
//            );

        } catch (Exception e) {
            log.error("删除对象失败: {}", e.getMessage());
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public void deleteUserFolder(String userId) {
        try {
            // 列出用户的所有文件
//            Iterable<Result<Item>> results = minioClient.listObjects(
//                ListObjectsArgs.builder()
//                    .bucket(bucketName)
//                    .prefix(userId + "/")
//                    .recursive(true)
//                    .build()
//            );
//
//            // 删除所有文件
//            for (Result<Item> result : results) {
//                Item item = result.get();
//                minioClient.removeObject(
//                    RemoveObjectArgs.builder()
//                        .bucket(bucketName)
//                        .object(item.objectName())
//                        .build()
//                );
//            }

        } catch (Exception e) {
            log.error("删除用户文件夹失败: {}", e.getMessage());
            throw new BusinessException("删除用户文件失败");
        }
    }

    /**
     * 压缩图片
     */
    private byte[] compressImage(byte[] imageData, int maxWidth, int maxHeight, float quality) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));

        // 计算新的尺寸
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // 创建压缩后的图片
        BufferedImage compressedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = compressedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, "jpg", baos);
        return baos.toByteArray();
    }

    /**
     * 标准化文件名
     */
    private String normalizeFilename(String filename) {
        if (StrUtil.isBlank(filename)) {
            return IdUtil.fastSimpleUUID();
        }

        // 移除扩展名
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            filename = filename.substring(0, lastDotIndex);
        }

        // 替换特殊字符
        return filename.replaceAll("[^a-zA-Z0-9\\-_]", "-");
    }

    /**
     * 根据类型获取扩展名
     */
    private String getExtensionByType(String type) {
        switch (type) {
            case "pictures":
            case "previews":
                return "jpg";
            case "resumes":
                return "pdf";
            default:
                return "bin";
        }
    }

    /**
     * 根据类型获取内容类型
     */
    private String getContentTypeByType(String type) {
        switch (type) {
            case "pictures":
            case "previews":
                return "image/jpeg";
            case "resumes":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }
}
