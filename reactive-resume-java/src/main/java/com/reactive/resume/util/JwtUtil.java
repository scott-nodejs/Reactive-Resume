package com.reactive.resume.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * JWT工具类
 *
 * @author Reactive Resume Team
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

  public String generateToken(String userId) {
    Date expiryDate = new Date(System.currentTimeMillis() + expiration);

    return JWT.create()
      .withSubject(userId)
      .withIssuedAt(new Date())
      .withExpiresAt(expiryDate)
      .sign(Algorithm.HMAC256(secret));
  }

    /**
     * 生成JWT令牌
     */
    public String generateToken(String userId, String email) {
        Date expiryDate = new Date(System.currentTimeMillis() + expiration);

        return JWT.create()
            .withSubject(userId)
            .withSubject(email)
            .withIssuedAt(new Date())
            .withExpiresAt(expiryDate)
            .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String userId) {
        Date expiryDate = new Date(System.currentTimeMillis() + expiration * 7); // 7天

        return JWT.create()
            .withSubject(userId)
            .withIssuedAt(new Date())
            .withExpiresAt(expiryDate)
            .withClaim("type", "refresh")
            .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 从令牌中获取用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            log.error("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从请求中获取用户ID
     */
    public String getUserIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (StrUtil.isNotBlank(token) && validateToken(token)) {
            return getUserIdFromToken(token);
        }
        return null;
    }

    /**
     * 从请求中获取令牌
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 获取令牌剩余有效时间（秒）
     */
    public long getTokenRemainingTime(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expiresAt = jwt.getExpiresAt();
            return (expiresAt.getTime() - System.currentTimeMillis()) / 1000;
        } catch (Exception e) {
            return 0;
        }
    }
}
