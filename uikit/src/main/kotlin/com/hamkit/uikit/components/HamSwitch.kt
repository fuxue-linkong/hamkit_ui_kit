package com.hamkit.uikit.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes

/**
 * 开关样式对象。
 */
@Immutable
data class HamSwitchStyle(
    /** 轨道宽度 */
    val trackWidth: Dp = 52.dp,
    /** 轨道高度 */
    val trackHeight: Dp = 30.dp,
    /** 滑块直径 */
    val thumbSize: Dp = 24.dp,
    /** 开启态轨道色；null 自动取强调色 */
    val checkedTrackColor: Color? = null,
)

/**
 * 玻璃开关（扁平风）：玻璃轨道 + 实底滑块，开启时滑块染强调色。
 *
 * @param checked 开关状态（受控）
 * @param onCheckedChange 状态变更回调；null 表示不可交互
 */
@Composable
fun HamSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glass: GlassSpec? = null,
    style: HamSwitchStyle = HamSwitchStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = LocalHamColors.current
    val shapes = LocalHamShapes.current
    // 滑块位移动画：弹簧曲线，扁平风的轻快手感
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 500f),
        label = "HamSwitchThumb",
    )
    val trackColor = if (checked) (style.checkedTrackColor ?: colors.accent) else Color.Transparent

    GlassSurface(
        modifier = modifier
            .hamPressable(interactionSource)
            .size(width = style.trackWidth, height = style.trackHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onCheckedChange != null,
                role = Role.Switch,
                onClick = { onCheckedChange?.invoke(!checked) },
            ),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 50.dp),
        shape = shapes.full,
    ) {
        Box(
            Modifier
                .background(trackColor.copy(alpha = if (checked) 0.9f else 0f), shapes.full),
        )
        // 滑块：按进度在轨道内平移（留 3dp 边距）
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset(x = lerpDp(3.dp, style.trackWidth - style.thumbSize - 3.dp, thumbPosition))
                .size(style.thumbSize)
                .clip(CircleShape)
                .background(if (checked) colors.onAccent else colors.textSecondary),
        )
    }
}

/** 线性插值工具：两个 Dp 之间按 fraction 插值 */
private fun lerpDp(start: Dp, end: Dp, fraction: Float): Dp =
    start + (end - start) * fraction
