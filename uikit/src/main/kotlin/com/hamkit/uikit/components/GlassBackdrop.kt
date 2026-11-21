package com.hamkit.uikit.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.hamkit.uikit.theme.LocalHamColors

/**
 * 玻璃衬底渐变背景。
 *
 * 玻璃拟态必须有高饱和度变化的底色衬托才能显出质感，本组件把主题
 * [com.hamkit.uikit.theme.HamColors.backdrop] 渲染为斜向多色渐变，
 * 作为页面最底层使用（玻璃组件浮于其上）。
 *
 * ```
 * Box {
 *     GlassBackdrop(Modifier.fillMaxSize())
 *     // 玻璃内容 ...
 * }
 * ```
 */
@Composable
fun GlassBackdrop(modifier: Modifier = Modifier) {
    val colors = LocalHamColors.current
    val stops = colors.backdrop
    Canvas(modifier = modifier.fillMaxSize()) {
        // 多锚点斜向渐变：从左上到右下铺满整屏
        drawRect(
            brush = Brush.linearGradient(
                colors = stops,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                tileMode = androidx.compose.ui.graphics.TileMode.Clamp,
            ),
        )
    }
}

/**
 * 便捷重载：直接指定渐变底色组（不依赖主题）。
 */
@Composable
fun GlassBackdrop(
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                tileMode = androidx.compose.ui.graphics.TileMode.Clamp,
            ),
        )
    }
}
