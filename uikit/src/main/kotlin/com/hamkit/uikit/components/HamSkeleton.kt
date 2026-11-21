package com.hamkit.uikit.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.LocalHamMotion
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamShapes

/**
 * 骨架屏样式对象。
 */
@Immutable
data class HamSkeletonStyle(
    /** 骨架块高度 */
    val height: Dp = 16.dp,
    /** 骨架块圆角 */
    val shape: Shape = RoundedCornerShape(8.dp),
)

/**
 * 玻璃骨架屏占位块（扁平风）：玻璃面 + 呼吸式明暗闪烁。
 *
 * 数据未加载时用作占位，多个骨架块组合即构成页面骨架。
 *
 * @param widthFraction 占父容器宽度比例（0..1）
 */
@Composable
fun HamSkeleton(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    glass: GlassSpec? = null,
    style: HamSkeletonStyle = HamSkeletonStyle(),
) {
    val motion = LocalHamMotion.current
    val shapes = LocalHamShapes.current
    // 呼吸动画：透明度在 0.35..0.85 之间往复
    val transition = rememberInfiniteTransition(label = "HamSkeleton")
    val breath by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = motion.shimmerMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "HamSkeletonBreath",
    )
    GlassSurface(
        modifier = modifier.fillMaxWidth(widthFraction),
        spec = glass ?: GlassSpec.Flat.copy(surfaceAlpha = breath.coerceIn(0.08f, 0.30f)),
        shape = style.shape,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(style.height),
        )
    }
}
