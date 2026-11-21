package com.hamkit.uikit.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.LocalHamMotion
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.LocalHamColors

/**
 * 玻璃单选按钮（扁平风）。
 *
 * 未选中：半透明圆环；选中：强调色实心点从中心弹出（spring 动画）。
 *
 * @param selected 是否选中（受控）
 * @param onClick 选择回调；null 不可交互
 */
@Composable
fun HamRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalHamColors.current
    val motion = LocalHamMotion.current
    // 选中点缩放进度：0（无）→ 1（满圆），弹簧参数跟随主题令牌
    val dotProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = spring(dampingRatio = motion.overshootDamping, stiffness = motion.overshootStiffness),
        label = "HamRadioDot",
    )
    val interaction = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .hamPressable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled && onClick != null,
                role = Role.RadioButton,
                onClick = { onClick?.invoke() },
            )
            .padding(6.dp),
    ) {
        Canvas(Modifier.size(22.dp)) {
            val stroke = 1.8.dp.toPx()
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            // 外环
            drawCircle(
                color = if (selected) colors.accent else colors.textSecondary.copy(alpha = 0.55f),
                radius = radius - stroke / 2,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke),
            )
            // 选中实心点：从中心按动画进度展开
            if (dotProgress > 0f) {
                drawCircle(
                    color = colors.accent,
                    radius = (radius - stroke * 2.2f) * dotProgress,
                    center = center,
                )
            }
        }
    }
}
