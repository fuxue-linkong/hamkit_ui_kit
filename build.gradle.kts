// 根构建脚本：统一插件版本声明，不在根应用插件
// AGP 9.0+ 内置 Kotlin 支持，kotlin-android 插件已不再需要
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
