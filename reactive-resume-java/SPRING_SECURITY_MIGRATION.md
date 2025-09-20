# Spring Security 迁移指南

## 🔄 从 Shiro 迁移到 Spring Security

由于 Apache Shiro 对 Spring Boot 3.x 和 Jakarta EE 的支持还不够完善，我们已将认证框架从 Shiro 迁移到 Spring Security。

## 📋 主要变更

### 1. **依赖更新**

#### 移除的依赖
```xml
<!-- 已移除 -->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
</dependency>
```

#### 新增的依赖
```xml
<!-- 新增 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 2. **配置类变更**

#### 删除的文件
- `ShiroConfig.java` - Shiro配置类
- `JwtRealm.java` - Shiro JWT Realm
- `JwtToken.java` - Shiro JWT Token
- `JwtFilter.java` - Shiro JWT 过滤器

#### 新增的文件
- `SecurityConfig.java` - Spring Security配置类
- `JwtAuthenticationFilter.java` - JWT认证过滤器
- `JwtAuthenticationEntryPoint.java` - JWT认证入口点
- `UserDetailsServiceImpl.java` - 用户详情服务

### 3. **认证流程变更**

#### Shiro 认证流程
```
请求 → JwtFilter → JwtRealm → 用户验证
```

#### Spring Security 认证流程
```
请求 → JwtAuthenticationFilter → UserDetailsService → 用户验证
```

### 4. **密码加密变更**

#### 原来 (MD5)
```java
String password = SecureUtil.md5(plainPassword);
```

#### 现在 (BCrypt)
```java
String password = passwordEncoder.encode(plainPassword);
boolean matches = passwordEncoder.matches(plainPassword, encodedPassword);
```

## 🔧 配置说明

### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // 安全过滤链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // 配置认证规则
        // 配置CORS
        // 配置JWT过滤器
    }
}
```

### JWT 认证过滤器
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) {
        // 从请求中提取JWT
        // 验证JWT
        // 设置认证上下文
    }
}
```

## 🛡️ 安全特性

### 1. **CORS 配置**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    return source;
}
```

### 2. **JWT 令牌处理**
- 支持从 Header 中获取 `Authorization: Bearer <token>`
- 支持从 Cookie 中获取 `accessToken`
- 自动验证令牌有效性
- 设置 Spring Security 认证上下文

### 3. **公开接口配置**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(
        "/api/auth/**",
        "/api/health/**",
        "/api/feature/**",
        "/api/translation/**",
        "/api/contributors/**",
        "/api/doc.html",
        "/api/swagger-ui/**"
    ).permitAll()
    .anyRequest().authenticated()
)
```

## 🔄 迁移步骤

### 1. **更新依赖**
```bash
mvn clean compile
```

### 2. **数据库密码迁移**
由于密码加密方式从 MD5 改为 BCrypt，需要重新设置用户密码：

```sql
-- 重置所有用户密码为 "123456"
UPDATE users SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxIcnYKuNuq6cDe';
```

或者在用户下次登录时要求重新设置密码。

### 3. **测试认证功能**
- 用户注册
- 用户登录
- JWT 令牌验证
- 受保护接口访问

## 📊 性能对比

| 特性 | Shiro | Spring Security |
|------|-------|-----------------|
| 学习曲线 | 较简单 | 中等 |
| 社区支持 | 一般 | 优秀 |
| Spring Boot 集成 | 一般 | 原生支持 |
| Jakarta EE 支持 | 有限 | 完整支持 |
| 文档质量 | 一般 | 优秀 |
| 更新频率 | 较慢 | 频繁 |

## 🔍 故障排除

### 1. **认证失败**
```
问题：JWT 令牌验证失败
解决：检查令牌格式和密钥配置
```

### 2. **CORS 问题**
```
问题：跨域请求被阻止
解决：检查 CORS 配置和允许的源
```

### 3. **密码验证失败**
```
问题：用户无法登录
解决：检查密码编码方式，可能需要重置密码
```

### 4. **权限不足**
```
问题：访问受保护资源被拒绝
解决：检查用户角色和权限配置
```

## 🎯 优势

### Spring Security 的优势：
- ✅ **原生支持** - Spring 生态系统的一部分
- ✅ **Jakarta EE** - 完整支持 Jakarta EE 规范
- ✅ **活跃维护** - 频繁更新和安全补丁
- ✅ **丰富功能** - 支持多种认证方式
- ✅ **优秀文档** - 详细的官方文档和示例
- ✅ **社区支持** - 大量的教程和解决方案

### 迁移后的改进：
- 🔒 **更强的安全性** - BCrypt 密码加密
- 🚀 **更好的性能** - 优化的过滤器链
- 🔧 **更易维护** - 标准化的配置方式
- 📈 **更好的扩展性** - 支持多种认证提供商

## 📞 支持

如果在迁移过程中遇到问题：

1. 检查日志文件中的错误信息
2. 验证配置文件的正确性
3. 确认依赖版本兼容性
4. 测试各个认证端点

---

**注意**: 此迁移确保了与 Spring Boot 3.x 和 Jakarta EE 的完全兼容性。
