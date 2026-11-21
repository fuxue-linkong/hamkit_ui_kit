package com.hamkit.uikit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamMotion
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 玻璃 Bottom Navigation（扁平风）。
 *
 * 悬浮在页面底部的玻璃长条，选中项使用强调色小圆点 + 高亮文字。
 *
 * @param items 标签项
 * @param selectedIndex 当前选中索引
 * @param onItemSelected 选择回调
 */
@Composable
fun HamBottomNavigation(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    glass: GlassSpec? = null,
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 24.dp, borderAlpha = 0.22f),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                // 选中色随状态过渡平滑变化（统一 stateChangeMillis 手感）
                val labelColor by animateColorAsState(
                    targetValue = if (selected) colors.accent else colors.textSecondary,
                    animationSpec = tween(durationMillis = LocalHamMotion.current.stateChangeMillis),
                    label = "HamNavLabelColor",
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickableNoRipple { onItemSelected(index) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = label,
                        style = typography.label,
                        color = labelColor,
                    )
                    // 选中指示圆点：尺寸 spring 弹出 / 收回
                    val dotScale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = LocalHamMotion.current.overshootDamping,
                            stiffness = LocalHamMotion.current.overshootStiffness,
                        ),
                        label = "HamNavDot",
                    )
                    Box(
                        Modifier
                            .padding(top = 4.dp)
                            .background(
                                color = colors.accent.copy(alpha = dotScale),
                                shape = androidx.compose.foundation.shape.CircleShape,
                            )
                            .size(5.dp)
                            .graphicsLayer {
                                scaleX = dotScale
                                scaleY = dotScale
                            },
                    )
                }
            }
        }
    }
}

/** 无涟漪点击 + 按压反馈（扁平风默认不带 Material 涟漪） */
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this
        .hamPressable(interactionSource)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
}
