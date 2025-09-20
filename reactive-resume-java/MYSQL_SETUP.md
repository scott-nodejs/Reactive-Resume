# MySQL 数据库配置指南

## 📋 前置要求

确保您已安装以下软件：
- MySQL 8.0+ 
- Java 17+
- Maven 3.6+

## 🚀 快速启动

### 1. 安装 MySQL

#### Windows
```bash
# 使用 Chocolatey
choco install mysql

# 或下载官方安装包
# https://dev.mysql.com/downloads/mysql/
```

#### macOS
```bash
# 使用 Homebrew
brew install mysql
brew services start mysql
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 2. 配置 MySQL

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE reactive_resume CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户（可选）
CREATE USER 'reactive_user'@'localhost' IDENTIFIED BY 'reactive_password';
GRANT ALL PRIVILEGES ON reactive_resume.* TO 'reactive_user'@'localhost';
FLUSH PRIVILEGES;

# 退出
EXIT;
```

### 3. 更新配置文件

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/reactive_resume?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root  # 或您创建的用户名
    password: password  # 您的密码
```

### 4. 初始化数据库

项目启动时会自动执行 `src/main/resources/sql/init.sql` 脚本。

或者手动执行：
```bash
mysql -u root -p reactive_resume < src/main/resources/sql/init.sql
```

### 5. 启动应用

```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/reactive-resume-java-1.0.0.jar
```

## 🔧 配置说明

### 数据库连接参数

| 参数 | 说明 |
|------|------|
| `useUnicode=true` | 启用Unicode支持 |
| `characterEncoding=utf8` | 字符编码 |
| `useSSL=false` | 禁用SSL（开发环境） |
| `serverTimezone=Asia/Shanghai` | 服务器时区 |
| `allowPublicKeyRetrieval=true` | 允许公钥检索 |

### MyBatis Plus 配置

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
  global-config:
    db-config:
      id-type: ASSIGN_UUID  # UUID主键
      logic-delete-field: deleted  # 逻辑删除字段
      db-type: mysql  # 数据库类型
```

## 📊 数据库表结构

### 用户表 (users)
- `id` - 用户ID (VARCHAR(36))
- `username` - 用户名 (VARCHAR(50))
- `email` - 邮箱 (VARCHAR(100))
- `name` - 姓名 (VARCHAR(100))
- `password` - 密码 (VARCHAR(255))
- `provider` - 认证提供商 (VARCHAR(20))
- `two_factor_enabled` - 双因子认证 (BOOLEAN)
- `created_at` - 创建时间 (DATETIME)
- `updated_at` - 更新时间 (DATETIME)

### 简历表 (resumes)
- `id` - 简历ID (VARCHAR(36))
- `title` - 简历标题 (VARCHAR(200))
- `slug` - 简历别名 (VARCHAR(100))
- `data` - 简历数据 (JSON)
- `visibility` - 可见性 (ENUM)
- `user_id` - 用户ID (VARCHAR(36))
- `created_at` - 创建时间 (DATETIME)
- `updated_at` - 更新时间 (DATETIME)

## 🛠️ 故障排除

### 常见问题

1. **连接被拒绝**
   ```
   解决方案：检查MySQL服务是否启动
   sudo systemctl status mysql
   ```

2. **时区问题**
   ```
   解决方案：在MySQL中设置时区
   SET GLOBAL time_zone = '+8:00';
   ```

3. **字符编码问题**
   ```
   解决方案：确保数据库使用utf8mb4编码
   ALTER DATABASE reactive_resume CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **权限问题**
   ```
   解决方案：检查用户权限
   SHOW GRANTS FOR 'your_user'@'localhost';
   ```

## 🔒 生产环境配置

### 安全建议

1. **使用专用数据库用户**
   ```sql
   CREATE USER 'reactive_app'@'localhost' IDENTIFIED BY 'strong_password_here';
   GRANT SELECT, INSERT, UPDATE, DELETE ON reactive_resume.* TO 'reactive_app'@'localhost';
   ```

2. **启用SSL连接**
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/reactive_resume?useSSL=true&requireSSL=true
   ```

3. **配置连接池**
   ```yaml
   spring:
     datasource:
       hikari:
         maximum-pool-size: 20
         minimum-idle: 5
         connection-timeout: 30000
         idle-timeout: 600000
         max-lifetime: 1800000
   ```

## 📈 性能优化

### 索引优化
```sql
-- 用户表索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_created_at ON users(created_at);

-- 简历表索引
CREATE INDEX idx_resumes_user_id ON resumes(user_id);
CREATE INDEX idx_resumes_visibility ON resumes(visibility);
CREATE INDEX idx_resumes_updated_at ON resumes(updated_at);
```

### 查询优化
- 使用合适的数据类型
- 避免SELECT *
- 使用LIMIT分页
- 定期分析慢查询日志

## 🔄 数据迁移

如果从PostgreSQL迁移到MySQL，请注意：

1. **数据类型差异**
   - `SERIAL` → `AUTO_INCREMENT`
   - `TEXT` → `TEXT` 或 `LONGTEXT`
   - `JSONB` → `JSON`

2. **语法差异**
   - `ILIKE` → `LIKE` (不区分大小写)
   - `LIMIT OFFSET` → `LIMIT offset, count`

3. **函数差异**
   - `NOW()` → `NOW()` (相同)
   - `EXTRACT()` → `EXTRACT()` (相同)

## 📞 支持

如果遇到问题，请检查：
1. MySQL服务状态
2. 网络连接
3. 用户权限
4. 防火墙设置
5. 应用日志

---

**注意**: 请根据您的实际环境调整配置参数。
