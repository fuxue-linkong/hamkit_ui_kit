package com.hamkit.uikit.effect

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.hamkit.uikit.theme.LocalHamMotion
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

/**
 * HamKit 动效令牌体系。
 *
 * 统一全库的按压反馈、入场与状态过渡手感，接入主题后可整体替换：
 * 通过 `LocalHamMotion provides ...` 覆盖当前子树的动效参数。
 *
 * 扁平风动效原则：快、轻、克制的微过冲——按下"缩一点、淡一点"，
 * 松手微过冲回弹；入场为淡入上滑，不使用大幅弹跳。
 */
data class HamMotion(
    /** 按压时缩放比例（1.0 = 不缩放） */
    val pressedScale: Float = 0.96f,
    /** 按压时透明度（1.0 = 不变淡） */
    val pressedAlpha: Float = 0.85f,
    /** 按压弹簧刚度（按下方向） */
    val springStiffness: Float = Spring.StiffnessMedium,
    /** 按压弹簧阻尼比（>1 无过冲） */
    val springDamping: Float = 1.1f,
    /** 入场淡入时长（ms），骨架屏等呼吸动效的基准节拍 */
    val fadeInMillis: Int = 220,
    /** 呼吸动效时长（ms），骨架屏 shimmer 用 */
    val shimmerMillis: Int = 1200,
    // ── v0.3 新增令牌 ──
    /** 回弹过冲弹簧阻尼比（<1 产生轻微过冲，"Q 弹"手感） */
    val overshootDamping: Float = 0.85f,
    /** 回弹过冲弹簧刚度 */
    val overshootStiffness: Float = Spring.StiffnessMedium,
    /** 入场动画时长（ms）：淡入 + 上滑 */
    val enterMillis: Int = 320,
    /** 列表交错入场间隔（ms）：index * 间隔形成瀑布节奏 */
    val enterStaggerMillis: Int = 40,
    /** 状态切换过渡时长（ms）：选中色 / 轨道色等 */
    val stateChangeMillis: Int = 180,
) {
    companion object {
        /** 默认动效：扁平风轻快手感 */
        val Default: HamMotion = HamMotion()

        /** 无过冲动效：适合正式、克制的界面（保险起见可一键切回） */
        val NoOvershoot: HamMotion = Default.copy(overshootDamping = 1.2f)
    }
}

/**
 * 统一按压反馈修饰符（玻璃风，无涟漪）。
 *
 * 效果：按下时 [HamMotion.pressedScale] 缩放 + [HamMotion.pressedAlpha] 变淡，
 * 松手后以微过冲弹簧回弹（v0.3）；全部经 graphicsLayer 实现，不触发重测量。
 *
 * @param interactionSource 交互源（与 clickable 共用以同步按压态）
 * @param motion 动效令牌；默认读取主题默认值
 * @param overshoot 是否启用回弹微过冲（false 则按压/回弹同用无过冲弹簧）
 */
fun Modifier.hamPressable(
    interactionSource: MutableInteractionSource,
    motion: HamMotion? = null,
    overshoot: Boolean = true,
): Modifier = composed {
    // 动效令牌：未显式指定时读取主题（CompositionLocal 在 composed 作用域内可访问）
    val effectiveMotion = motion ?: LocalHamMotion.current
    // 收集按压态，驱动 scale/alpha 动画
    val pressed by interactionSource.collectIsPressedAsState()
    // 回弹弹簧：松手时启用微过冲（若开启），按下方向保持无过冲的干脆手感
    val releaseSpring = if (overshoot) {
        spring<Float>(dampingRatio = effectiveMotion.overshootDamping, stiffness = effectiveMotion.overshootStiffness)
    } else {
        spring<Float>(dampingRatio = effectiveMotion.springDamping, stiffness = effectiveMotion.springStiffness)
    }
    val pressSpring = spring<Float>(dampingRatio = effectiveMotion.springDamping, stiffness = effectiveMotion.springStiffness)
    // 按下/松手切换动画规格：松手方向用回弹弹簧
    val scale by animateFloatAsState(
        targetValue = if (pressed) effectiveMotion.pressedScale else 1f,
        animationSpec = if (pressed) pressSpring else releaseSpring,
        label = "HamPressScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (pressed) effectiveMotion.pressedAlpha else 1f,
        animationSpec = tween(durationMillis = effectiveMotion.fadeInMillis, easing = LinearEasing),
        label = "HamPressAlpha",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/**
 * 入场动画修饰符：淡入 + 上滑，支持列表交错节奏（v0.3）。
 *
 * 首次组合时触发一次，适合卡片瀑布、分区内容出现：
 *
 * ```
 * items.forEachIndexed { i, card ->
 *     Card(Modifier.hamEnter(index = i)) { ... }   // 每张卡错开 40ms
 * }
 * ```
 *
 * @param index 交错序号：入场延迟 = index * enterStaggerMillis
 * @param fromOffsetY 初始下移量（px），默认 24px 上滑入场
 * @param motion 动效令牌
 */
fun Modifier.hamEnter(
    index: Int = 0,
    fromOffsetY: Float = 24f,
    motion: HamMotion? = null,
): Modifier = composed {
    // 动效令牌：未显式指定时读取主题
    val effectiveMotion = motion ?: LocalHamMotion.current
    // 首帧置 false，经交错延迟后翻转为 true 触发动画
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index * effectiveMotion.enterStaggerMillis).toLong())
        visible = true
    }
    // progress: 0（未入场）→ 1（完全入场）
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = effectiveMotion.enterMillis, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "HamEnter",
    )
    graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * fromOffsetY
        // 轻微缩放配合入场，视觉更柔和（0.98 → 1.0）
        val s = 0.98f + 0.02f * progress
        scaleX = s
        scaleY = s
    }
}

/**
 * 便捷重载：内部自建 InteractionSource，用于不需要外部观察交互态的场景。
 */
@Composable
fun rememberPressInteraction(): MutableInteractionSource = remember { MutableInteractionSource() }
