-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    picture VARCHAR(500),
    password VARCHAR(255),
    locale VARCHAR(10) DEFAULT 'zh-CN',
    email_verified BOOLEAN DEFAULT FALSE,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    provider VARCHAR(20) DEFAULT 'email',
    two_factor_secret VARCHAR(255),
    two_factor_backup_codes TEXT,
    refresh_token VARCHAR(500),
    reset_token VARCHAR(255) UNIQUE,
    verification_token VARCHAR(255),
    last_signed_in TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建简历表
CREATE TABLE IF NOT EXISTS resumes (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    data JSONB DEFAULT '{}',
    visibility VARCHAR(20) DEFAULT 'private',
    locked BOOLEAN DEFAULT FALSE,
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(user_id, slug)
);

-- 创建统计表
CREATE TABLE IF NOT EXISTS statistics (
    id VARCHAR(36) PRIMARY KEY,
    views INTEGER DEFAULT 0,
    downloads INTEGER DEFAULT 0,
    resume_id VARCHAR(36) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_resumes_user_id ON resumes(user_id);
CREATE INDEX IF NOT EXISTS idx_resumes_visibility ON resumes(visibility);
CREATE INDEX IF NOT EXISTS idx_statistics_resume_id ON statistics(resume_id);

-- 插入默认数据
INSERT INTO users (id, username, email, name, provider, email_verified) 
VALUES ('admin-user-id', 'admin', 'admin@reactive-resume.com', 'Administrator', 'email', true)
ON CONFLICT (email) DO NOTHING;
