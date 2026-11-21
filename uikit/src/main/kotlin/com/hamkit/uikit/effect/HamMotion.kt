package com.hamkit.uikit.effect

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * HamKit 动效令牌体系。
 *
 * 统一全库的按压反馈与运动手感，接入主题后可整体替换：
 * 通过 `LocalHamMotion provides ...` 覆盖当前子树的动效参数。
 *
 * 扁平风动效原则：快、轻、无弹性过冲——按压只做"缩一点、淡一点"，
 * 松手立即回弹，不使用大阻尼振荡。
 */
data class HamMotion(
    /** 按压时缩放比例（1.0 = 不缩放） */
    val pressedScale: Float = 0.96f,
    /** 按压时透明度（1.0 = 不变淡） */
    val pressedAlpha: Float = 0.85f,
    /** 按压回弹弹簧刚度 */
    val springStiffness: Float = Spring.StiffnessMediumHigh,
    /** 按压回弹弹簧阻尼比（>1 无过冲，贴合扁平风） */
    val springDamping: Float = 1.1f,
    /** 入场淡入时长（ms），骨架屏等呼吸动效的基准节拍 */
    val fadeInMillis: Int = 220,
    /** 呼吸动效时长（ms），骨架屏 shimmer 用 */
    val shimmerMillis: Int = 1200,
) {
    companion object {
        /** 默认动效：扁平风轻快手感 */
        val Default: HamMotion = HamMotion()
    }
}

/**
 * 统一按压反馈修饰符（玻璃风，无涟漪）。
 *
 * 效果：按下时 [HamMotion.pressedScale] 缩放 + [HamMotion.pressedAlpha] 变淡，
 * 弹簧回弹；全部经 graphicsLayer 实现，不触发重测量，性能开销极低。
 *
 * ```
 * Box(Modifier.hamPressable(interactionSource).clickable(...)) { ... }
 * ```
 */
fun Modifier.hamPressable(
    interactionSource: MutableInteractionSource,
    motion: HamMotion = HamMotion.Default,
): Modifier = composed {
    // 收集按压态，驱动 scale/alpha 动画
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) motion.pressedScale else 1f,
        animationSpec = spring(dampingRatio = motion.springDamping, stiffness = motion.springStiffness),
        label = "HamPressScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (pressed) motion.pressedAlpha else 1f,
        animationSpec = tween(durationMillis = motion.fadeInMillis, easing = LinearEasing),
        label = "HamPressAlpha",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/**
 * 便捷重载：内部自建 InteractionSource，用于不需要外部观察交互态的场景。
 */
@Composable
fun rememberPressInteraction(): MutableInteractionSource = remember { MutableInteractionSource() }
