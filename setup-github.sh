#!/bin/bash

# GitHub Actions 快速设置脚本

echo "🚀 GitHub Actions 快速设置"
echo "=========================="
echo ""

# 检查是否已初始化 git
if [ ! -d ".git" ]; then
    echo "📦 初始化 Git 仓库..."
    git init
    git add .
    git commit -m "初始提交: Android 收纳 App"
    echo "✅ Git 仓库初始化完成"
else
    echo "ℹ️  Git 仓库已存在"
fi

echo ""
echo "📝 下一步操作："
echo "1. 在 GitHub 创建新仓库"
echo "2. 运行以下命令："
echo ""
echo "   git branch -M main"
echo "   git remote add origin https://github.com/你的用户名/仓库名.git"
echo "   git push -u origin main"
echo ""
echo "3. 推送成功后，GitHub Actions 会自动开始构建"
echo "4. 访问仓库的 'Actions' 标签页查看构建状态"
echo ""
echo "📖 详细配置请查看 GITHUB_ACTIONS_GUIDE.md"
echo ""
