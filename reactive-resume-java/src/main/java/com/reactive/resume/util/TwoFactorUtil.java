package com.reactive.resume.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 双因子认证工具类
 * 
 * @author Reactive Resume Team
 */
@Slf4j
@Component
public class TwoFactorUtil {

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    /**
     * 生成双因子认证密钥
     */
    public String generateSecret() {
        byte[] buffer = new byte[20];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) RandomUtil.randomInt(256);
        }
        return Base64.getEncoder().encodeToString(buffer);
    }

    /**
     * 生成TOTP验证码
     */
    public String generateTOTP(String secret) {
        long timeStep = System.currentTimeMillis() / 30000; // 30秒时间窗口
        return generateTOTP(secret, timeStep);
    }

    /**
     * 生成TOTP验证码
     */
    public String generateTOTP(String secret, long timeStep) {
        try {
            byte[] key = Base64.getDecoder().decode(secret);
            byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();
            
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(new SecretKeySpec(key, HMAC_SHA1));
            byte[] hash = mac.doFinal(data);
            
            int offset = hash[hash.length - 1] & 0xf;
            int binary = ((hash[offset] & 0x7f) << 24) |
                        ((hash[offset + 1] & 0xff) << 16) |
                        ((hash[offset + 2] & 0xff) << 8) |
                        (hash[offset + 3] & 0xff);
            
            int otp = binary % DIGITS_POWER[6];
            return String.format("%06d", otp);
            
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("生成TOTP失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证TOTP验证码
     */
    public boolean verifyTOTP(String secret, String code) {
        if (secret == null || code == null) {
            return false;
        }

        long currentTimeStep = System.currentTimeMillis() / 30000;
        
        // 检查当前时间窗口和前后各一个时间窗口
        for (int i = -1; i <= 1; i++) {
            String expectedCode = generateTOTP(secret, currentTimeStep + i);
            if (code.equals(expectedCode)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 生成备用验证码
     */
    public List<String> generateBackupCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codes.add(RandomUtil.randomString("0123456789", 8));
        }
        return codes;
    }

    /**
     * 验证备用验证码
     */
    public boolean verifyBackupCode(List<String> backupCodes, String code) {
        return backupCodes != null && backupCodes.contains(code);
    }

    /**
     * 生成QR码URL
     */
    public String generateQRCodeUrl(String secret, String email, String issuer) {
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s",
            issuer, email, secret, issuer
        );
    }
}
