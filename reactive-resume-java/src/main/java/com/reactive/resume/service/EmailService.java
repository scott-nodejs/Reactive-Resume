package com.reactive.resume.service;

/**
 * 邮件服务接口
 * 
 * @author Reactive Resume Team
 */
public interface EmailService {

    /**
     * 发送验证邮件
     */
    void sendVerificationEmail(String email, String token);

    /**
     * 发送密码重置邮件
     */
    void sendPasswordResetEmail(String email, String token);

    /**
     * 发送欢迎邮件
     */
    void sendWelcomeEmail(String email, String name);

    /**
     * 发送通知邮件
     */
    void sendNotificationEmail(String email, String subject, String content);
}
