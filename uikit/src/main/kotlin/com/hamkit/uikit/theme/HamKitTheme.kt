package com.hamkit.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.hamkit.uikit.effect.GlassDefaults
import com.hamkit.uikit.effect.GlassBlurProvider
import com.hamkit.uikit.effect.HamMotion

/** 当前颜色体系 */
val LocalHamColors = staticCompositionLocalOf { HamColors.Light }

/** 当前字体体系 */
val LocalHamTypography = staticCompositionLocalOf { HamTypography.Default }

/** 当前形状体系 */
val LocalHamShapes = staticCompositionLocalOf { HamShapes.Default }

/** 当前动效体系 */
val LocalHamMotion = staticCompositionLocalOf { HamMotion.Default }

/**
 * HamKit 主题入口：装配颜色 / 字体 / 形状 / 玻璃规范 / 动效 五大底层体系。
 *
 * 所有参数均可选 —— 只传需要改的部分，其余使用默认值；玻璃规范会同时
 * 写入 [GlassDefaults.glassSpec] 作为全局默认。
 *
 * ```
 * // 全量自定义
 * HamKitTheme(
 *     colors = myColors,
 *     glass = GlassSpec(blurRadius = 24.dp, surfaceAlpha = 0.12f),
 * ) { ... }
 *
 * // 内置预设，开箱即用
 * HamKitThemePreset(HamThemePreset.Ocean) { ... }
 * ```
 */
@Composable
fun HamKitTheme(
    colors: HamColors = LocalHamColors.current,
    typography: HamTypography = LocalHamTypography.current,
    shapes: HamShapes = LocalHamShapes.current,
    glass: GlassSpec = GlassSpec.Defaults,
    motion: HamMotion = HamMotion.Default,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalHamColors provides colors,
        LocalHamTypography provides typography,
        LocalHamShapes provides shapes,
        LocalHamMotion provides motion,
        GlassDefaults.glassSpec provides glass,
        content = content,
    )
}

/**
 * 内置风格预设。
 *
 * @property name 预设名
 * @property light 浅色配色
 * @property dark 深色配色
 * @property glass 配套玻璃规范
 */
enum class HamThemePreset(
    val light: HamColors,
    val dark: HamColors,
    val glass: GlassSpec,
) {
    /** 默认：清晨蓝 */
    Sky(HamColors.Light, HamColors.Dark, GlassSpec.Defaults),

    /** 海洋：青绿冷色 */
    Ocean(
        light = HamColors.Light.copy(backdrop = listOf(Color(0xFF7FDCC5), Color(0xFF9DB8FF))),
        dark = HamColors.Dark.copy(backdrop = listOf(Color(0xFF062B2B), Color(0xFF123B66))),
        glass = GlassSpec.Thin,
    ),

    /** 落日：暖橙粉 */
    Sunset(
        light = HamColors.Light.copy(backdrop = listOf(Color(0xFFFFC3A0), Color(0xFFFF8FB1))),
        dark = HamColors.Dark.copy(backdrop = listOf(Color(0xFF3D1030), Color(0xFF4A1D52))),
        glass = GlassSpec.Thick,
    ),

    /** 极简扁平：最轻玻璃感 */
    Mono(HamColors.Light, HamColors.Dark, GlassSpec.Flat),
}

/**
 * 以预设启动主题，一行代码获得完整玻璃风格（含配套动效）。
 *
 * @param preset 风格预设
 * @param isDark 是否使用深色配色
 */
@Composable
fun HamKitThemePreset(
    preset: HamThemePreset,
    isDark: Boolean = false,
    content: @Composable () -> Unit,
): Unit = HamKitTheme(
    colors = if (isDark) preset.dark else preset.light,
    glass = preset.glass,
    content = content,
)
