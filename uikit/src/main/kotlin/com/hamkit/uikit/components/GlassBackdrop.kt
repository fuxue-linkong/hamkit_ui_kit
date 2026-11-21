package com.hamkit.uikit.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import com.hamkit.uikit.theme.LocalHamColors

/**
 * 背景录制层共享通道：玻璃组件经此读取"玻璃背后的画面"实现透视模糊。
 *
 * [GlassBackdrop] 录制自身内容后写入本 CompositionLocal；
 * [com.hamkit.uikit.effect.GlassSurface] 读取并模糊绘制。未注册时玻璃走降级绘制。
 */
val LocalBackdropLayer = compositionLocalOf<GraphicsLayer?> { null }

/** 漂浮色斑参数（内部）：决定一个色斑的初始位置 / 半径比例 / 漂移方向 */
internal data class BackdropBlob(
    val xFraction: Float,
    val yFraction: Float,
    val radiusFraction: Float,
    val driftX: Float,
    val driftY: Float,
)

/**
 * 玻璃衬底渐变背景（v0.4 增强版）。
 *
 * 三项职责：
 * 1. 渲染主题 backdrop 渐变，玻璃拟态的底色衬托；
 * 2. （[decor] = true）绘制漂浮色斑并做缓慢漂移动画——玻璃后面有可见内容，
 *    透视模糊效果肉眼可辨；
 * 3. **录制自身内容**到 GraphicsLayer 并经 [LocalBackdropLayer] 共享，
 *    玻璃组件（GlassSurface）据此实现真实透视模糊（API 31+）。
 *
 * ```
 * Box {
 *     GlassBackdrop(Modifier.fillMaxSize())   // 页面最底层
 *     // 玻璃内容浮于其上，自动获得透视模糊
 * }
 * ```
 *
 * @param decor 是否绘制漂浮色斑（内容页可关闭以节省合成开销）
 */
@Composable
fun GlassBackdrop(
    modifier: Modifier = Modifier,
    decor: Boolean = true,
) {
    val colors = LocalHamColors.current
    val stops = colors.backdrop

    // 背景快照层：玻璃组件模糊的"背后画面"来源
    val backdropLayer = rememberGraphicsLayer()
    // 漂浮色斑：位置 / 半径 / 漂移方向固定，动效只改相位
    val blobs = remember(decor) {
        if (decor) {
            listOf(
                BackdropBlob(0.82f, 0.12f, 0.38f, 1f, 0.6f),
                BackdropBlob(0.10f, 0.82f, 0.42f, -0.7f, -1f),
                BackdropBlob(0.55f, 0.55f, 0.30f, 0.9f, -0.8f),
            )
        } else {
            emptyList()
        }
    }
    // 色斑漂移动画：-1..1 往复，各斑不同相位错开
    val drift = rememberInfiniteTransition(label = "HamBackdropBlob")
    val blobPhase by drift.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "HamBackdropDrift",
    )

    // 色斑颜色：从主题渐变色衍生的高饱和同族色，保证与底色和谐
    val blobColors = remember(stops) {
        stops.take(3).mapIndexed { i, c ->
            when (i % 3) {
                0 -> c.copy(alpha = 0.85f).copy(red = (c.red + 0.15f).coerceAtMost(1f))
                1 -> c.copy(alpha = 0.75f).copy(green = (c.green + 0.2f).coerceAtMost(1f))
                else -> c.copy(alpha = 0.7f).copy(blue = (c.blue + 0.25f).coerceAtMost(1f))
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            // 录制本组件内容：玻璃层 drawLayer 时即可拿到"背后的画面"
            .drawWithContent {
                backdropLayer.record(size) { this@drawWithContent.drawContent() }
                drawLayer(backdropLayer)
            },
    ) {
        // 1) 斜向多色渐变底
        drawRect(
            brush = Brush.linearGradient(
                colors = stops,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                tileMode = androidx.compose.ui.graphics.TileMode.Clamp,
            ),
        )
        // 2) 漂浮色斑：柔和径向光斑，相位驱动漂移
        blobs.forEachIndexed { i, blob ->
            val phase = blobPhase * (if (i % 2 == 0) 1f else -1f)  // 相邻色斑反相漂移
            val cx = (blob.xFraction + blob.driftX * phase * 0.06f) * size.width
            val cy = (blob.yFraction + blob.driftY * phase * 0.06f) * size.height
            val r = blob.radiusFraction * size.minDimension
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColors[i % blobColors.size], blobColors[i % blobColors.size].copy(alpha = 0f)),
                    center = Offset(cx, cy),
                    radius = r,
                ),
                radius = r,
                center = Offset(cx, cy),
            )
        }
    }

    // 经 CompositionLocal 共享录制层给玻璃组件：Canvas 外再包一层 provider，
    // 使玻璃子树（页面内容）都能读取到背景快照
    CompositionLocalProvider(LocalBackdropLayer provides backdropLayer) {
        androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize())
    }
}

/**
 * 便捷重载：直接指定渐变底色组（不依赖主题，不注册透视层）。
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
