package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors

/**
 * FAB 样式对象。
 */
@Immutable
data class HamFabStyle(
    /** 圆钮直径 */
    val size: androidx.compose.ui.unit.Dp = 56.dp,
    /** 前景内容色；null 自动取主题 */
    val contentColor: Color? = null,
)

/**
 * 玻璃悬浮按钮（扁平风）。
 *
 * 强调形态：强调色实底圆钮；玻璃形态：玻璃圆钮。
 * 图标槽开放为 composable，配合 Canvas 绘制的任意图形。
 *
 * @param onClick 点击回调
 * @param content 图标内容（如 "+" 文本或自绘图形）
 */
@Composable
fun HamFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = true,
    enabled: Boolean = true,
    glass: GlassSpec? = null,
    style: HamFabStyle = HamFabStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val colors = LocalHamColors.current
    val pressModifier = modifier
        .hamPressable(interactionSource)
        .size(style.size)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick,
        )

    if (emphasized) {
        // 强调 FAB：强调色实底圆钮
        Box(
            modifier = pressModifier.background(
                color = if (enabled) colors.accent else colors.accent.copy(alpha = 0.38f),
                shape = CircleShape,
            ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier.size(style.size),
                contentAlignment = Alignment.Center,
                // 内容色由调用方经 LocalHamColors 决定，这里提供默认前景色环境
            ) { content() }
        }
    } else {
        // 玻璃 FAB：玻璃圆钮
        GlassSurface(
            modifier = pressModifier,
            spec = glass ?: GlassSpec(),
            shape = CircleShape,
        ) {
            Box(
                Modifier.size(style.size),
                contentAlignment = Alignment.Center,
            ) { content() }
        }
    }
}
