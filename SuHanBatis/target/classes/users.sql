-- src/main/resources/create-users-table.sql
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username   VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password   VARCHAR(255) NOT NULL COMMENT '密码',
    email      VARCHAR(100) UNIQUE COMMENT '邮箱',
    full_name  VARCHAR(100) COMMENT '全名',
    phone      VARCHAR(20) COMMENT '电话号码',
    status     TINYINT   DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT ='用户表';

-- 创建索引
CREATE INDEX idx_username ON users (username);
CREATE INDEX idx_email ON users (email);

-- 插入简化测试数据
INSERT INTO users (username, password, email, full_name, phone)
VALUES ('admin', 'admin123', 'admin@example.com', '管理员', '13800138000'),
       ('user1', 'password1', 'user1@example.com', '用户一', '13800138001'),
       ('user2', 'password2', 'user2@example.com', '用户二', '13800138002'),
       ('test', 'test123', 'test@example.com', '测试用户', '13800138003'),
       ('demo', 'demo123', 'demo@example.com', '演示用户', '13800138004'),
       ('guest', 'guest123', 'guest@example.com', '访客用户', '13800138005'),
       ('student', 'study123', 'student@example.com', '学生用户', '13800138006'),
       ('teacher', 'teach123', 'teacher@example.com', '教师用户', '13800138007');
