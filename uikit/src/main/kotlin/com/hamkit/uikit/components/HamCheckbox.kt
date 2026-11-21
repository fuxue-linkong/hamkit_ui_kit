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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes

/**
 * 玻璃复选框（扁平风）。
 *
 * 未选中：半透明方框 + 细描边；选中：强调色填充 + 白色对勾（Canvas 绘制，零图标依赖）。
 *
 * @param checked 勾选状态（受控）
 * @param onCheckedChange 状态回调；null 不可交互
 */
@Composable
fun HamCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalHamColors.current
    val shapes = LocalHamShapes.current
    // 勾选填充进度动画：方框内强调色从 0 缩放到满格
    val fillProgress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = 1.1f, stiffness = 480f),
        label = "HamCheckboxFill",
    )
    val interaction = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .hamPressable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled && onCheckedChange != null,
                role = Role.Checkbox,
                onClick = { onCheckedChange?.invoke(!checked) },
            )
            .padding(6.dp),
    ) {
        Canvas(Modifier.size(22.dp)) {
            val stroke = 1.8.dp.toPx()
            // 底框：半透明表面
            drawRoundRect(
                color = colors.textSecondary.copy(alpha = 0.28f),
                cornerRadius = CornerRadius(6.dp.toPx()),
            )
            // 选中填充：强调色，按动画进度从中心展开
            if (fillProgress > 0f) {
                drawRoundRect(
                    color = colors.accent,
                    cornerRadius = CornerRadius(6.dp.toPx()),
                    size = Size(size.width * fillProgress, size.height * fillProgress),
                    topLeft = Offset(
                        (size.width - size.width * fillProgress) / 2,
                        (size.height - size.height * fillProgress) / 2,
                    ),
                )
            }
            // 细描边勾勒方框
            drawRoundRect(
                color = colors.textSecondary.copy(alpha = 0.55f),
                cornerRadius = CornerRadius(6.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke),
            )
            // 对勾：填充展开完成后淡入（进度 > 0.6 开始绘制）
            if (fillProgress > 0.6f) {
                val checkAlpha = ((fillProgress - 0.6f) / 0.4f).coerceIn(0f, 1f)
                val checkColor = colors.onAccent.copy(alpha = checkAlpha)
                val w = size.width
                val h = size.height
                // 两段线构成对勾
                drawLine(
                    color = checkColor,
                    start = Offset(w * 0.26f, h * 0.54f),
                    end = Offset(w * 0.44f, h * 0.72f),
                    strokeWidth = stroke * 1.4f,
                )
                drawLine(
                    color = checkColor,
                    start = Offset(w * 0.44f, h * 0.72f),
                    end = Offset(w * 0.76f, h * 0.32f),
                    strokeWidth = stroke * 1.4f,
                )
            }
        }
    }
}
