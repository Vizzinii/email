# 邮件系统后端项目

## 简介

本项目是一个邮件系统的后端服务，基于 Spring Boot 构建，提供了用户管理、邮件发送和接收、附件上传等功能。该项目旨在实现一个基本的邮件系统，支持用户登录认证、邮件收发、邮件附件管理等。

## 主要功能

- 用户注册与登录
- 发送邮件
- 接收邮件
- 附件上传与下载
- 邮件保存至草稿箱
- 查询未读邮件数量

## 环境要求

- JDK 8 或以上版本
- Maven 3.6 或以上版本
- MySQL 5.7 或以上版本

## 安装步骤

1. 克隆项目到本地：
   ```bash
   git clone https://github.com/your-repo/email-system-backend.git
   cd email-system-backend

2. 配置数据库：
- 在 MySQL 中创建一个数据库，例如 email_system。
- 修改 src/main/resources/application.properties 文件，配置数据库连接信息:
  ```bash
  spring.datasource.url=jdbc:mysql://localhost:3306/email_system?useSSL=false
  spring.datasource.username=root
  spring.datasource.password=yourpassword
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect

3. 构建项目：
   ```bash
   mvn clean install

## 运行项目

1. 使用 Maven 启动项目：
   ```bash
   mvn clean install

2. 项目启动后，后端服务将运行在 http://localhost:8080

## 项目结构
  ```bash
  email-system-backend/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/email/
│   │   │   ├── controller/       # 控制器
│   │   │   ├── entity/           # 实体类
│   │   │   ├── repository/       # 仓库接口
│   │   │   ├── service/          # 服务层
│   │   │   └── Application.java # 主应用程序入口
│   │   └── resources/
│   │       ├── application.properties # 应用程序配置文件
│   │       └── static/           # 静态资源
│   └── test/
│       └── java/com/example/email/ # 测试代码
│
├── pom.xml                        # Maven 项目对象模型文件
└── README.md                      # 项目说明文件