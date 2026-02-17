// Top-level build file
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}

// 配置国内镜像源 - 解决国内下载慢的问题
allprojects {
    repositories {
        // 阿里云镜像 - 国内推荐
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        
        // 腾讯云镜像
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        
        // 华为云镜像
        maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
        
        // JitPack - 用于GitHub项目
        maven { url = uri("https://jitpack.io") }
        
        // 原始仓库(作为备用)
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
