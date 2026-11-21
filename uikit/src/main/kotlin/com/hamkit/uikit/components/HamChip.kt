package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * Chip 样式对象。
 */
@Immutable
data class HamChipStyle(
    /** 选中态文字色；null 自动取 onAccent */
    val selectedContentColor: Color? = null,
    /** 未选中态文字色；null 自动取 textPrimary */
    val contentColor: Color? = null,
)

/**
 * 玻璃筛选 Chip（扁平风）：可点击的单选标签。
 *
 * @param selected 选中态：使用强调色实底（与按钮强调态同一视觉语言）
 */
@Composable
fun HamChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glass: GlassSpec? = null,
    style: HamChipStyle = HamChipStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current

    if (selected) {
        // 选中：强调色实底药丸，扁平醒目
        Text(
            text = text,
            style = typography.label,
            color = style.selectedContentColor ?: colors.onAccent,
            modifier = modifier
                .background(
                    color = colors.accent,
                    shape = RoundedCornerShape(50),
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp, vertical = 9.dp),
        )
    } else {
        // 未选中：玻璃表面药丸
        GlassSurface(
            modifier = modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
            spec = glass ?: GlassSpec.Thin,
            shape = RoundedCornerShape(50),
        ) {
            Text(
                text = text,
                style = typography.label,
                color = style.contentColor ?: colors.textPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}
