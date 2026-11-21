package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 头像样式对象。
 */
@Immutable
data class HamAvatarStyle(
    /** 头像直径 */
    val size: Dp = 42.dp,
    /** 字母头像的底色；null 自动取强调色 */
    val backgroundColor: Color? = null,
    /** 字母头像的前景色；null 自动取 onAccent */
    val contentColor: Color? = null,
)

/**
 * 玻璃头像（扁平风）：字母底色圆 + 半透明描边环。
 *
 * 图片头像可通过 [content] 传入自定义圆形内容（如 AsyncImage 的替代实现）。
 *
 * @param label 字母头像显示的文字（通常取名字首字）；content 非空时可传 null
 * @param content 自定义头像内容；非空时忽略 [label]
 */
@Composable
fun HamAvatar(
    modifier: Modifier = Modifier,
    label: String? = null,
    glass: GlassSpec? = null,
    style: HamAvatarStyle = HamAvatarStyle(),
    content: (@Composable () -> Unit)? = null,
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    GlassSurface(
        modifier = modifier.size(style.size),
        spec = glass ?: GlassSpec.Thin.copy(borderAlpha = 0.4f),
        shape = CircleShape,
    ) {
        Box(
            Modifier.size(style.size),
            contentAlignment = Alignment.Center,
        ) {
            if (content != null) {
                content()
            } else {
                // 字母头像：强调色底 + 首字
                Box(
                    Modifier
                        .size(style.size)
                        .background(
                            color = style.backgroundColor ?: colors.accent,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    label?.let {
                        Text(
                            text = it.take(1),
                            style = typography.subtitle,
                            color = style.contentColor ?: colors.onAccent,
                        )
                    }
                }
            }
        }
    }
}
