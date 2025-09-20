package com.reactive.resume.service.impl;

import com.reactive.resume.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String email, String token) {
        String subject = "验证您的邮箱地址 - Reactive Resume";
        String verificationUrl = frontendUrl + "/auth/verify-email?token=" + token;
        
        String content = String.format(
            "您好！\n\n" +
            "感谢您注册 Reactive Resume！\n\n" +
            "请点击以下链接验证您的邮箱地址：\n" +
            "%s\n\n" +
            "如果您没有注册账户，请忽略此邮件。\n\n" +
            "此链接将在24小时后过期。\n\n" +
            "祝好，\n" +
            "Reactive Resume 团队",
            verificationUrl
        );

        sendEmail(email, subject, content);
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String subject = "重置您的密码 - Reactive Resume";
        String resetUrl = frontendUrl + "/auth/reset-password?token=" + token;
        
        String content = String.format(
            "您好！\n\n" +
            "我们收到了重置您密码的请求。\n\n" +
            "请点击以下链接重置您的密码：\n" +
            "%s\n\n" +
            "如果您没有请求重置密码，请忽略此邮件。\n\n" +
            "此链接将在1小时后过期。\n\n" +
            "祝好，\n" +
            "Reactive Resume 团队",
            resetUrl
        );

        sendEmail(email, subject, content);
    }

    @Override
    public void sendWelcomeEmail(String email, String name) {
        String subject = "欢迎使用 Reactive Resume！";
        
        String content = String.format(
            "您好 %s！\n\n" +
            "欢迎使用 Reactive Resume - 免费开源的简历构建器！\n\n" +
            "您现在可以：\n" +
            "• 创建专业的简历\n" +
            "• 选择多种精美模板\n" +
            "• 实时预览和编辑\n" +
            "• 导出为PDF格式\n" +
            "• 分享您的简历链接\n\n" +
            "立即开始创建您的第一份简历：\n" +
            "%s/dashboard\n\n" +
            "如果您有任何问题，请随时联系我们。\n\n" +
            "祝好，\n" +
            "Reactive Resume 团队",
            name, frontendUrl
        );

        sendEmail(email, subject, content);
    }

    @Override
    public void sendNotificationEmail(String email, String subject, String content) {
        sendEmail(email, subject, content);
    }

    /**
     * 发送邮件的通用方法
     */
    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("邮件发送成功: {} -> {}", subject, to);
            
        } catch (Exception e) {
            log.error("邮件发送失败: {} -> {}, 错误: {}", subject, to, e.getMessage());
            // 这里可以选择抛出异常或者记录到失败队列中重试
        }
    }
}
