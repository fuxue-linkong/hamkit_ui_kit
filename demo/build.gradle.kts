plugins {
    alias(libs.plugins.android.application)
    // AGP 9.0+ 内置 Kotlin 支持，无需 kotlin-android 插件
    alias(libs.plugins.kotlin.compose)
}

android {
    // 本机/CI 复用已装 build-tools，避免 AGP 联网自动下载卡住
    buildToolsVersion = "37.0.0"
    namespace = "com.hamkit.demo"
    compileSdk = "android-37.1"

    defaultConfig {
        applicationId = "com.hamkit.demo"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.4.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

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
}

dependencies {
    implementation(project(":uikit"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.core.ktx)
    debugImplementation(libs.compose.ui.tooling)
}
