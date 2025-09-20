//package com.reactive.resume.config;
//
//import io.minio.MinioClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Minio配置类
// *
// * @author Reactive Resume Team
// */
//@Slf4j
//@Configuration
//public class MinioConfig {
//
//    @Value("${minio.endpoint}")
//    private String endpoint;
//
//    @Value("${minio.access-key}")
//    private String accessKey;
//
//    @Value("${minio.secret-key}")
//    private String secretKey;
//
//    @Bean
//    public MinioClient minioClient() {
//        try {
//            MinioClient client = MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKey, secretKey)
//                .build();
//
//            log.info("Minio客户端初始化成功: {}", endpoint);
//            return client;
//
//        } catch (Exception e) {
//            log.error("Minio客户端初始化失败: {}", e.getMessage());
//            throw new RuntimeException("Minio配置错误", e);
//        }
//    }
//}
