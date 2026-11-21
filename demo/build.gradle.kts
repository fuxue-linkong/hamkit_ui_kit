plugins {
    alias(libs.plugins.android.application)
    // AGP 9.0+ 内置 Kotlin 支持，无需 kotlin-android 插件
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hamkit.demo"
    compileSdk = 37

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
    kotlinOptions {
        jvmTarget = "17"
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
