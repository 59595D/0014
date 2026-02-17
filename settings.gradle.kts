pluginManagement {
    repositories {
        // 原始仓库(优先，KSP插件需要)
        google()
        mavenCentral()
        gradlePluginPortal()
        // 阿里云镜像(作为加速)
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 原始仓库(优先)
        google()
        mavenCentral()
        // 阿里云镜像(作为加速)
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
    }
}

rootProject.name = "StorageApp"
include(":app")
