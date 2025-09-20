package com.reactive.resume;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Reactive Resume 应用启动类
 * 
 * @author Reactive Resume Team
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.reactive.resume.mapper")
public class ReactiveResumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveResumeApplication.class, args);
        System.out.println("🚀 Reactive Resume Backend is running on port 8080");
        System.out.println("📚 API Documentation: http://localhost:8080/api/doc.html");
    }
}

