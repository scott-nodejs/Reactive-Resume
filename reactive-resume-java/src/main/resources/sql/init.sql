-- Reactive Resume 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS reactive_resume CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE reactive_resume;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    picture VARCHAR(500) COMMENT '头像URL',
    password VARCHAR(255) COMMENT '密码',
    locale VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言设置',
    email_verified BOOLEAN DEFAULT FALSE COMMENT '邮箱是否已验证',
    two_factor_enabled BOOLEAN DEFAULT FALSE COMMENT '是否启用双因子认证',
    provider VARCHAR(20) DEFAULT 'email' COMMENT '认证提供商',
    two_factor_secret VARCHAR(255) COMMENT '双因子认证密钥',
    two_factor_backup_codes TEXT COMMENT '双因子认证备用码',
    refresh_token VARCHAR(500) COMMENT '刷新令牌',
    reset_token VARCHAR(255) COMMENT '重置密码令牌',
    verification_token VARCHAR(255) COMMENT '邮箱验证令牌',
    last_signed_in DATETIME COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_reset_token (reset_token),
    INDEX idx_verification_token (verification_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入默认用户（用于开发测试）
INSERT IGNORE INTO users (
    id, username, email, name, password, locale, email_verified, 
    provider, created_at, updated_at
) VALUES (
    'demo-user-id', 
    'demo', 
    'demo@example.com', 
    'Demo User', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFdEWdgJKj6yKjrByCpfF5a', -- 密码: demo123
    'zh-CN', 
    TRUE, 
    'email', 
    NOW(), 
    NOW()
);

-- 简历表
CREATE TABLE IF NOT EXISTS resumes (
    id VARCHAR(36) PRIMARY KEY COMMENT '简历ID',
    title VARCHAR(200) NOT NULL COMMENT '简历标题',
    slug VARCHAR(100) NOT NULL COMMENT '简历别名',
    data JSON COMMENT '简历数据',
    visibility ENUM('public', 'private') DEFAULT 'private' COMMENT '可见性',
    locked BOOLEAN DEFAULT FALSE COMMENT '是否锁定',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_slug (user_id, slug),
    INDEX idx_user_id (user_id),
    INDEX idx_visibility (visibility),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表';

-- 简历统计表
CREATE TABLE IF NOT EXISTS resume_statistics (
    id VARCHAR(36) PRIMARY KEY COMMENT '统计ID',
    resume_id VARCHAR(36) NOT NULL UNIQUE COMMENT '简历ID',
    views INT DEFAULT 0 COMMENT '浏览次数',
    downloads INT DEFAULT 0 COMMENT '下载次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历统计表';

-- 插入scott用户（用于开发测试）
INSERT IGNORE INTO users (
    id, username, email, name, password, locale, email_verified,
    provider, created_at, updated_at
) VALUES (
    'scott@123.com',
    'scott',
    'scott@123.com',
    'Scott User',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFdEWdgJKj6yKjrByCpfF5a', -- 密码: demo123
    'zh-CN',
    TRUE,
    'email',
    NOW(),
    NOW()
);

-- 插入默认管理员用户
INSERT IGNORE INTO users (
    id, username, email, name, password, email_verified, provider
) VALUES (
    'admin-user-id-001', 
    'admin', 
    'admin@reactive-resume.com', 
    'Administrator', 
    'e10adc3949ba59abbe56e057f20f883e', -- 密码: 123456 (MD5)
    TRUE, 
    'email'
);
