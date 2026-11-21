plugins {
    alias(libs.plugins.android.library)
    // AGP 9.0+ 内置 Kotlin 支持，无需 kotlin-android 插件
    alias(libs.plugins.kotlin.compose)
    // 发布 AAR 到 Maven 仓库（本地 / 远端）
    `maven-publish`
}

android {
    namespace = "com.hamkit.uikit"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    // Java/Kotlin 目标统一 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // AGP 9 / KGP 2.2：kotlinOptions DSL 已移除，改用 compilerOptions
    @Suppress("UnstableApiUsage")
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // Compose BOM 统一版本，库只依赖官方 Compose 基础层
    implementation(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.ui.graphics)
    api(libs.compose.foundation)
    api(libs.compose.material3)

    debugImplementation(libs.compose.ui.tooling)
    api(libs.compose.ui.tooling.preview)
}

// maven 发布配置：publishToMavenLocal 即可本地验证接入
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.hamkit"
            artifactId = "uikit"
            version = "0.4.0"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
