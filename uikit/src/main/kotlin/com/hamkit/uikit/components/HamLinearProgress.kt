package com.hamkit.uikit.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes

/**
 * 进度条样式对象。
 */
@Immutable
data class HamProgressStyle(
    /** 轨道高度 */
    val trackHeight: Dp = 6.dp,
    /** 填充色；null 自动取强调色 */
    val color: Color? = null,
    /** 轨道底色 alpha（扁平风保持很淡） */
    val trackAlpha: Float = 0.22f,
)

/**
 * 玻璃线性进度条（扁平风）：玻璃轨道 + 强调色填充。
 *
 * 进度变化自带平滑动画，避免生硬跳变。
 *
 * @param progress 目标进度 0..1
 */
@Composable
fun HamLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    glass: GlassSpec? = null,
    style: HamProgressStyle = HamProgressStyle(),
) {
    val colors = LocalHamColors.current
    val shapes = LocalHamShapes.current
    // 平滑动画：进度跳变时 250ms 缓动到位
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = "HamLinearProgress",
    )
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 8.dp, borderAlpha = 0.18f),
        shape = shapes.small,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(style.trackHeight),
        ) {
            // 轨道底
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(style.trackHeight)
                    .clip(shapes.small)
                    .background(colors.textSecondary.copy(alpha = style.trackAlpha)),
            )
            // 填充条
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(animated)
                    .height(style.trackHeight)
                    .clip(shapes.small)
                    .background(style.color ?: colors.accent),
            )
        }
    }
}
