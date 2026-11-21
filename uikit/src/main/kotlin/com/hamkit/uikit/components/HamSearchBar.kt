package com.hamkit.uikit.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 搜索框样式对象。
 */
@Immutable
data class HamSearchBarStyle(
    /** 高度 */
    val height: Dp = 46.dp,
    /** 提示色；null 自动取主题 */
    val hintColor: Color? = null,
    /** 输入文字色；null 自动取主题 */
    val textColor: Color? = null,
)

/**
 * 玻璃搜索框（扁平风）：胶囊玻璃面 + 左侧放大镜图标（Canvas 绘制，零图标依赖）。
 *
 * @param value 输入内容（受控）
 * @param onValueChange 输入回调
 * @param hint 占位提示
 */
@Composable
fun HamSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "搜索",
    enabled: Boolean = true,
    glass: GlassSpec? = null,
    style: HamSearchBarStyle = HamSearchBarStyle(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    val shapes = LocalHamShapes.current
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 50.dp),
        shape = shapes.full,
    ) {
        Row(
            Modifier
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 放大镜：Canvas 圆环 + 手柄，纯绘制不引图标库
            androidx.compose.foundation.Canvas(Modifier.size(18.dp)) {
                val stroke = 1.8.dp.toPx()
                val lensRadius = size.minDimension * 0.32f
                val center = androidx.compose.ui.geometry.Offset(
                    size.width * 0.42f,
                    size.height * 0.42f,
                )
                drawCircle(
                    color = colors.textSecondary,
                    radius = lensRadius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke),
                )
                drawLine(
                    color = colors.textSecondary,
                    start = androidx.compose.ui.geometry.Offset(
                        center.x + lensRadius * 0.72f,
                        center.y + lensRadius * 0.72f,
                    ),
                    end = androidx.compose.ui.geometry.Offset(
                        size.width * 0.88f,
                        size.height * 0.88f,
                    ),
                    strokeWidth = stroke * 1.3f,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = (typography.body).copy(color = style.textColor ?: colors.textPrimary),
                cursorBrush = SolidColor(colors.accent),
                interactionSource = interactionSource,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, top = 13.dp, bottom = 13.dp),
                decorationBox = { innerTextField ->
                    Box {
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
}
