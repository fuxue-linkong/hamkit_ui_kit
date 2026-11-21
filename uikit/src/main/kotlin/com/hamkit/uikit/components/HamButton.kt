package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 按钮样式对象：三级自定义中最细的一级。
 *
 * 只需覆写关心的字段，其余保持默认即可。
 */
@Immutable
data class HamButtonStyle(
    /** 内容水平内边距 */
    val contentPadding: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 13.dp),
    /** 文字颜色；null 时自动取主题（主按钮取 onAccent，次按钮取 textPrimary） */
    val contentColor: Color? = null,
    /** 最小高度 */
    val minHeight: Int = 48,
    /** 是否整行铺满宽度 */
    val fillMaxWidth: Boolean = false,
)

/**
 * 玻璃按钮（扁平风）。
 *
 * 自定义次序：`style`/`glass` 实参 > `GlassSpecOverride` 作用域 > `HamKitTheme` 全局。
 *
 * @param text 按钮文字
 * @param onClick 点击回调
 * @param emphasized 是否为强调按钮：true 使用强调色实底（无模糊，最扁平醒目）；
 *                   false 使用玻璃表面
 * @param glass 玻璃规范覆盖；null 读取全局
 * @param style 样式细调
 */
@Composable
fun HamButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    emphasized: Boolean = false,
    glass: GlassSpec? = null,
    style: HamButtonStyle = HamButtonStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    // 强调按钮走实底强调色（最扁平），普通按钮走玻璃表面
    val baseModifier = if (style.fillMaxWidth) modifier.fillMaxWidth() else modifier

    if (emphasized) {
        // 强调按钮：强调色实底 + 药丸圆角，无玻璃层（扁平原则：色块分明）
        Box(
            modifier = baseModifier
                .hamPressable(interactionSource)
                .heightIn(min = style.minHeight.dp)
                .background(
                    color = if (enabled) colors.accent else colors.accent.copy(alpha = 0.38f),
                    shape = RoundedCornerShape(50),
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = typography.button,
                color = style.contentColor ?: colors.onAccent,
            )
        }
    } else {
        // 玻璃按钮：GlassSurface 承载玻璃质感
        GlassSurface(
            modifier = baseModifier
                .hamPressable(interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                ),
            spec = glass ?: GlassSpec(),
            shape = RoundedCornerShape(50),
        ) {
            Box(
                modifier = Modifier
                    .heightIn(min = style.minHeight.dp)
                    .padding(style.contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = typography.button,
                    color = style.contentColor ?: colors.textPrimary,
                )
            }
        }
    }
}
