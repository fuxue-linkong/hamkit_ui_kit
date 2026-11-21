package com.hamkit.uikit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 卡片样式对象（三级自定义的最细级）。
 */
@Immutable
data class HamCardStyle(
    /** 内容内边距 */
    val contentPadding: Dp = 18.dp,
    /** 标题颜色；null 自动取主题 */
    val titleColor: Color? = null,
    /** 正文颜色；null 自动取主题 */
    val bodyColor: Color? = null,
)

/**
 * 玻璃卡片（扁平风）：标题 + 内容槽的最常用信息容器。
 *
 * @param title 标题；传 null 则不显示
 * @param glass 玻璃规范覆盖；null 读取全局（默认使用薄玻璃）
 */
@Composable
fun HamCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    glass: GlassSpec? = null,
    shape: Shape? = null,
    style: HamCardStyle = HamCardStyle(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    GlassSurface(
        modifier = modifier,
        spec = glass ?: GlassSpec.Thin,
        shape = shape,
    ) {
        Column(modifier = Modifier.padding(style.contentPadding)) {
            title?.let {
                Text(
                    text = it,
                    style = typography.subtitle,
                    color = style.titleColor ?: colors.textPrimary,
                )
            }
            content()
        }
    }
}
