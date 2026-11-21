package com.hamkit.uikit.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 输入框样式对象。
 */
@Immutable
data class HamTextFieldStyle(
    /** 提示文字色；null 自动取主题 */
    val hintColor: Color? = null,
    /** 输入文字颜色；null 自动取主题 */
    val textColor: Color? = null,
    /** 内边距 */
    val contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    /** 输入文字样式；null 自动取主题 body */
    val textStyle: TextStyle? = null,
)

/**
 * 玻璃输入框（扁平风）：玻璃表面 + 单行/多行输入。
 *
 * @param value 输入内容（受控状态）
 * @param onValueChange 输入回调
 * @param hint 占位提示文字
 */
@Composable
fun HamTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    glass: GlassSpec? = null,
    style: HamTextFieldStyle = HamTextFieldStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    val shapes = LocalHamShapes.current
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        spec = glass ?: GlassSpec.Thin,
        shape = shapes.medium,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = (style.textStyle ?: typography.body).copy(
                color = style.textColor ?: colors.textPrimary,
            ),
            cursorBrush = SolidColor(colors.accent),
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            modifier = Modifier.padding(style.contentPadding),
            decorationBox = { innerTextField ->
                Box {
                    // 占位提示：输入为空时显示
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = typography.body,
                            color = style.hintColor ?: colors.textSecondary,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}
