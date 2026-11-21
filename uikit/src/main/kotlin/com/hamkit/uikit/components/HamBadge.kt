package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 徽标样式对象。
 */
@Immutable
data class HamBadgeStyle(
    /** 自定义底色；null 自动取强调色 */
    val background: Color? = null,
    /** 自定义文字色；null 自动取主题 onAccent */
    val contentColor: Color? = null,
)

/**
 * 玻璃徽标（扁平风）：小号药丸标签，用于计数、状态标记。
 *
 * @param text 徽标文字（如数量、状态）
 */
@Composable
fun HamBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: HamBadgeStyle = HamBadgeStyle(),
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    Text(
        text = text,
        style = typography.label,
        color = style.contentColor ?: colors.onAccent,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(
                color = style.background ?: colors.accent,
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}
