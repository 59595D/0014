# GitHub Actions 构建配置指南

本指南将帮助您使用 GitHub Actions 在线构建 APK。

## 🚀 快速开始

### 方法一：自动构建（推荐）

1. **创建 GitHub 仓库**
   ```bash
   git init
   git add .
   git commit -m "初始提交"
   git branch -M main
   git remote add origin https://github.com/你的用户名/仓库名.git
   git push -u origin main
   ```

2. **配置 Workflow 文件**
   - 将 `.github/workflows/android.yml` 文件提交到仓库
   - GitHub 会自动检测并运行构建

3. **查看构建状态**
   - 访问仓库的 "Actions" 标签页
   - 选择最新的工作流运行

### 方法二：手动触发构建

1. 进入仓库的 **Actions** 标签页
2. 选择 **Android CI** 工作流
3. 点击 **Run workflow**
4. 选择构建类型：
   - `debug` - 调试版本（快速构建）
   - `release` - 发布版本（需要签名）

## 📦 下载 APK

构建完成后：

1. 进入对应的工作流运行页面
2. 滚动到底部的 **Artifacts** 区域
3. 下载 APK 文件：
   - `app-debug` - Debug 版本
   - `app-release` - Release 版本

## 🔐 配置签名（Release 版本）

如果您需要生成签名的 Release APK，需要配置仓库密钥：

### 1. 生成签名密钥库

```bash
keytool -genkey -v -keystore release-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release
```

### 2. 配置 GitHub Secrets

在仓库设置中添加以下 Secrets：

- `KEYSTORE_BASE64` - 密钥库的 base64 编码
- `KEYSTORE_PASSWORD` - 密钥库密码
- `KEY_ALIAS` - 密钥别名
- `KEY_PASSWORD` - 密钥密码

### 3. 获取 base64 编码

```bash
# macOS/Linux
base64 -i release-keystore.jks | pbcopy

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release-keystore.jks")) | Set-Clipboard
```

## 📋 构建配置说明

### 工作流触发条件

- **Push** - 推送到 main/master 分支时自动构建 Debug 版本
- **Pull Request** - PR 到 main/master 分支时自动构建 Debug 版本
- **手动触发** - 可选择构建 Debug 或 Release 版本

### 构建优化

- 使用 JDK 17
- 启用 Gradle 缓存
- 并行构建
- 国内镜像源已配置（自动应用）

### 构建产物保留

- Debug 版本保留 30 天
- Release 版本保留 90 天

## 🛠️ 常见问题

### 构建失败

1. **检查 Gradle 版本**
   - 确保版本兼容
   - 查看错误日志

2. **依赖下载失败**
   - 国内镜像源已配置
   - 确保网络连接正常

3. **内存不足**
   - 减少 `org.gradle.jvmargs` 的内存值

### Release 版本未签名

如果不配置签名密钥，仍会生成未签名的 Release APK，但无法直接安装。

### 构建时间过长

- 首次构建约需 5-10 分钟
- 后续构建使用缓存约 2-3 分钟
- Debug 版本比 Release 版本快

## 📱 测试 APK

1. 下载 APK 到手机
2. 允许安装未知来源应用
3. 直接点击安装

## 🔄 自动发布到 Release

启用自动发布功能后，每次手动触发 Release 构建会：
- 自动创建 GitHub Release
- 上传签名的 APK
- 生成版本号（基于运行次数）

## 💡 最佳实践

1. **使用 Debug 版本进行开发测试**
   - 构建速度快
   - 包含调试信息

2. **使用 Release 版本进行正式发布**
   - 优化性能
   - 移除调试代码
   - 签名验证

3. **定期清理旧的构建产物**
   - 避免存储空间浪费
   - 自动过期机制已启用

4. **配置分支保护**
   - 要求 PR 审查
   - 自动运行 CI
