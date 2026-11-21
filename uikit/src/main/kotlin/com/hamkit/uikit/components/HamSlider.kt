package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.sliderSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes

/**
 * 滑杆样式对象。
 */
@Immutable
data class HamSliderStyle(
    /** 轨道高度 */
    val trackHeight: Dp = 6.dp,
    /** 滑块直径 */
    val thumbSize: Dp = 22.dp,
    /** 已填充轨道色；null 自动取强调色 */
    val activeTrackColor: Color? = null,
) {
    /** 滑块直径像素值换算（供拖动换算使用） */
    internal fun thumbSizePx(density: androidx.compose.ui.unit.Density): Float =
        with(density) { thumbSize.toPx() }
}

/**
 * 玻璃滑杆（扁平风）：玻璃轨道 + 实底圆点滑块。
 *
 * 拖动手势经 pointerInput 自实现，无任何第三方依赖；
 * 拖动仅响应水平方向，避免与列表滚动冲突。
 *
 * @param value 当前值（受控）
 * @param onValueChange 拖动回调
 * @param valueRange 取值范围
 */
@Composable
fun HamSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    glass: GlassSpec? = null,
    style: HamSliderStyle = HamSliderStyle(),
) {
    val colors = LocalHamColors.current
    val shapes = LocalHamShapes.current
    val density = LocalDensity.current
    // 记录轨道实际宽度（px），滑块定位与拖动换算都依赖它
    var trackSize by remember { mutableFloatStateOf(0f) }
    // 归一化进度 0..1
    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    val spanPx = (trackSize - with(density) { style.thumbSize.toPx() }).coerceAtLeast(0f)

    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 50.dp),
        shape = shapes.full,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(style.trackHeight + style.thumbSize)
                .onSizeChanged { size: IntSize -> trackSize = size.width.toFloat() }
                .let { m ->
                    // 手势层：仅在 enabled 时挂载拖动检测
                    if (enabled) {
                        m.pointerInput(valueRange) {
                            detectHorizontalDragGestures { change, _ ->
                                change.consume()
                                // 拖动位置换算为归一化进度并映射回取值范围
                                val newFraction = if (spanPx > 0f) {
                                    (change.position.x - style.thumbSizePx(density) / 2)
                                        .coerceIn(0f, spanPx) / spanPx
                                } else 0f
                                onValueChange(valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start))
                            }
                        }
                    } else {
                        m
                    }
                }
                .sliderSemantics(value, valueRange) { newValue -> onValueChange(newValue); true },
        ) {
            // 底层轨道：半透明
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(style.trackHeight)
                    .clip(shapes.full)
                    .background(colors.textSecondary.copy(alpha = 0.25f)),
            )
            // 已填充轨道：强调色
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(fraction)
                    .height(style.trackHeight)
                    .clip(shapes.full)
                    .background(style.activeTrackColor ?: colors.accent),
            )
            // 滑块：沿轨道水平移动
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = with(density) { (spanPx * fraction).toDp() })
                    .size(style.thumbSize)
                    .clip(CircleShape)
                    .background(if (enabled) (style.activeTrackColor ?: colors.accent) else colors.textSecondary),
            )
        }
    }
}
