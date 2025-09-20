# Reactive Resume Java Backend

这是 Reactive Resume 的 Java Spring Boot 后端实现，使用 Apache Shiro + MyBatis Plus 技术栈。

## 🚀 技术栈

- **Spring Boot 3.2.0** - 主框架
- **Apache Shiro 1.13.0** - 安全认证框架
- **MyBatis Plus 3.5.5** - ORM框架
- **MySQL/PostgreSQL** - 数据库
- **Redis** - 缓存和Session存储
- **Knife4j** - API文档
- **JWT** - 无状态认证
- **Minio** - 对象存储

## 📁 项目结构

```
src/main/java/com/reactive/resume/
├── config/                 # 配置类
│   ├── ShiroConfig.java    # Shiro安全配置
│   └── SwaggerConfig.java  # API文档配置
├── controller/             # 控制器层
│   ├── AuthController.java # 认证控制器
│   ├── UserController.java # 用户控制器
│   └── ResumeController.java # 简历控制器
├── service/                # 服务层
│   ├── AuthService.java    # 认证服务接口
│   └── impl/               # 服务实现
├── mapper/                 # 数据访问层
│   ├── UserMapper.java     # 用户Mapper
│   └── ResumeMapper.java   # 简历Mapper
├── entity/                 # 实体类
│   ├── User.java           # 用户实体
│   └── Resume.java         # 简历实体
├── dto/                    # 数据传输对象
├── shiro/                  # Shiro相关
│   ├── JwtRealm.java       # JWT认证域
│   ├── JwtToken.java       # JWT令牌
│   └── JwtFilter.java      # JWT过滤器
├── util/                   # 工具类
└── common/                 # 公共类
```

## 🛠️ 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+ 或 PostgreSQL 13+
- Redis 6.0+

### 2. 数据库配置

#### MySQL
```sql
CREATE DATABASE reactive_resume CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### PostgreSQL
```sql
CREATE DATABASE reactive_resume;
```

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/reactive_resume
    username: your_username
    password: your_password
```

### 4. 启动应用

```bash
# 克隆项目
git clone <repository-url>
cd reactive-resume-java

# 编译项目
mvn clean compile

# 运行数据库初始化脚本
mysql -u username -p reactive_resume < src/main/resources/sql/init.sql

# 启动应用
mvn spring-boot:run
```

### 5. 访问应用

- **API文档**: http://localhost:8080/api/doc.html
- **健康检查**: http://localhost:8080/api/health

## 📚 API 接口

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/logout` | 用户登出 |
| POST | `/api/auth/refresh` | 刷新令牌 |
| POST | `/api/auth/forgot-password` | 忘记密码 |
| POST | `/api/auth/reset-password` | 重置密码 |

### 用户接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/user/me` | 获取当前用户信息 |
| PATCH | `/api/user/me` | 更新用户信息 |
| DELETE | `/api/user/me` | 删除用户账户 |

### 简历接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/resume` | 创建简历 |
| GET | `/api/resume` | 获取用户所有简历 |
| GET | `/api/resume/{id}` | 获取指定简历 |
| PATCH | `/api/resume/{id}` | 更新简历 |
| DELETE | `/api/resume/{id}` | 删除简历 |

## 🔒 安全配置

项目使用 Apache Shiro + JWT 实现无状态认证：

1. **JWT令牌**: 用户登录后获得访问令牌和刷新令牌
2. **Cookie存储**: 令牌存储在HttpOnly Cookie中，提高安全性
3. **自动刷新**: 访问令牌过期时自动使用刷新令牌获取新令牌
4. **双因子认证**: 支持TOTP双因子认证

## 🚀 部署

### Docker 部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/reactive-resume-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 环境变量

```bash
# 数据库配置
DATABASE_URL=jdbc:mysql://localhost:3306/reactive_resume
DATABASE_USERNAME=username
DATABASE_PASSWORD=password

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT配置
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# 邮件配置
MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 📝 开发指南

### 添加新的API接口

1. 在 `controller` 包中创建控制器
2. 在 `service` 包中定义服务接口
3. 在 `service.impl` 包中实现服务
4. 在 `mapper` 包中定义数据访问接口
5. 在 `dto` 包中定义数据传输对象

### 数据库迁移

使用 Flyway 或 Liquibase 进行数据库版本管理：

```sql
-- V1.1__add_new_column.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License
