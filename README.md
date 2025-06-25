# AWS 管理平台

## 项目概述
本项目是一个基于 Spring Boot 后端和 Vue.js 前端构建的 AWS Lightsail管理平台，为用户提供了 AWS 密钥管理、实例管理、用户认证和密码修改等功能。通过前后端分离的架构，实现了高效的模块化管理和良好的用户体验。

## 功能特性
### 前端功能
- **密钥管理**：支持查看、新增、编辑和删除 AWS 密钥，密钥信息以密码形式隐藏显示，保障信息安全。
- **实例管理**：可查看实例的相关信息，按地区统计实例数量，支持搜索功能。
- **用户认证**：包含登录认证检查和退出功能，保障系统安全性。
- **密码修改**：用户可在平台内修改登录密码，增强账户信息安全。

### 后端功能
- **数据持久化**：使用 MySQL 数据库存储用户信息、AWS 密钥和实例信息。
- **缓存管理**：利用 Redis 进行用户信息和配置的缓存，提高系统性能。
- **定时任务**：通过 Spring Scheduler 执行定时任务，如实例状态检查和 IP 信息清理。
- **异常处理**：统一的异常处理机制，确保系统稳定运行。

## 技术栈
### 后端
- **框架**：Spring Boot 3.4.0
- **数据库**：MySQL 8.0.28
- **缓存**：Redis 6.x
- **ORM 框架**：MyBatis-Plus 3.5.9
- **代码生成**：Forest 1.6.4
- **文档生成**：SpringDoc OpenAPI 2.7.0、Knife4j 4.5.0

### 基础设施
- **容器化**：Docker
- **构建工具**：Maven

### 前端
- **框架**：Vue.js 2.6.14
- **UI 组件库**：Element UI
- **前端框架**：Vue.js 2.6.14
- **UI 框架**：Element UI
- **HTTP 请求库**：Axios
- **后端语言**：未明确（需实现相应接口）
- **数据库**：MySQL（根据 SQL 文件推测）

### 基础设施
- **容器化**：Docker，用于快速部署和隔离应用环境。
- **构建工具**：Maven，用于项目的依赖管理和构建。

## 项目结构
```plaintext
/Users/a12580/Desktop/aws/源码/server/
├── Dockerfile               # Docker 构建文件
├── pom.xml                  # Maven 项目配置文件
├── run.sh                   # 项目启动脚本
├── src/
│   ├── main/
│   │   ├── java/            # Java 源代码
│   │   │   └── com/
│   │   │       └── tk/
│   │   │           ├── aws/
│   │   │           ├── common/
│   │   │           └── server/
│   │   ├── resources/       # 资源文件
│   │   │   ├── application.yml  # 项目配置文件
│   │   │   └── templates/    # 前端页面模板
│   │   │       └── aws/
│   │   │           ├── index.html
│   │   │           ├── instances.html
│   │   │           └── keys.html
│   │   └── webapp/
│   └── test/                # 测试代码
└── mysql.sql                # 数据库初始化脚本
```

## 运行步骤
### 环境准备
确保以下软件已安装并配置好：

- JDK 21 或更高版本
- MySQL 8.0.28
- Redis 6.x
- Docker（可选，用于容器化部署）
- Maven 3.x

数据库初始化：
- 登录 MySQL 数据库，创建新的数据库：
```bash
CREATE DATABASE myt_tk;
```
- 执行 mysql.sql 脚本初始化数据库表结构和数据：
```bash
mysql -u [数据库用户名] -p [数据库名] < mysql.sql
```
### Docker 部署
- 构建 Docker 镜像：
```bash
docker build -t myt_tk .
```
- 运行 Docker 容器：
```bash
docker run -p 8080:8080 myt_tk
```
### API 文档
项目使用 SpringDoc OpenAPI 和 Knife4j 生成 API 文档，启动项目后，可访问以下地址查看：
- SpringDoc OpenAPI：http://localhost:8080/swagger-ui.html
- Knife4j: http://localhost:8000/doc.html




