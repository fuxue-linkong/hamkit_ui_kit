package com.hamkit.uikit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 列表行样式对象。
 */
@Immutable
data class HamListItemStyle(
    /** 行高 */
    val height: Dp = 60.dp,
    /** 水平内边距 */
    val horizontalPadding: Dp = 16.dp,
    /** 标题色；null 自动取主题 */
    val titleColor: Color? = null,
    /** 副标题色；null 自动取主题 */
    val subtitleColor: Color? = null,
)

/**
 * 玻璃列表行（扁平风）：三槽位布局 leading / content / trailing。
 *
 * 点击行为可选：不传 [onClick] 时为纯展示行（无按压反馈）。
 *
 * @param title 主标题
 * @param subtitle 副标题；null 不显示
 * @param leading 头部槽（常放 HamAvatar）
 * @param trailing 尾部槽（常放开关或徽标）
 */
@Composable
fun HamListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    glass: GlassSpec? = null,
    style: HamListItemStyle = HamListItemStyle(),
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    val interaction = remember { MutableInteractionSource() }
    // 可点击行叠加按压反馈；纯展示行不挂手势
    val rowModifier = if (onClick != null) {
        modifier
            .hamPressable(interaction)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
    } else {
        modifier
    }

    GlassSurface(
        modifier = rowModifier.fillMaxWidth(),
        spec = glass ?: GlassSpec.Thin,
    ) {
        Row(
            Modifier
                .height(style.height)
                .padding(horizontal = style.horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.let {
                it()
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.subtitle,
                    color = style.titleColor ?: colors.textPrimary,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = typography.caption,
                        color = style.subtitleColor ?: colors.textSecondary,
                    )
                }
            }
            trailing?.let {
                Spacer(Modifier.width(12.dp))
                it()
            }
        }
    }
}
