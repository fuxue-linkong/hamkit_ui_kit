package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.LocalHamMotion
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 分段控件样式对象。
 */
@Immutable
data class HamSegmentedStyle(
    /** 整体高度 */
    val height: Dp = 40.dp,
    /** 选中指示块颜色；null 自动取强调色 */
    val thumbColor: Color? = null,
)

/**
 * 玻璃分段控件（扁平风）：玻璃槽底 + 强调色指示块滑动切换。
 *
 * @param items 分段文案
 * @param selectedIndex 当前选中索引（受控）
 * @param onSegmentSelected 选择回调
 */
@Composable
fun HamSegmented(
    items: List<String>,
    selectedIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    glass: GlassSpec? = null,
    style: HamSegmentedStyle = HamSegmentedStyle(),
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    val density = LocalDensity.current
    // 读取主题动效令牌：指示块滑动与状态色过渡统一手感
    val motion = LocalHamMotion.current

    GlassSurface(
        modifier = modifier.height(style.height),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 14.dp),
        shape = RoundedCornerShape(14.dp),
    ) {
        Box {
            // 滑动指示块：垫在文字层下方，选中索引经动画平滑滑动
            BoxWithConstraints(Modifier.matchParentSize()) {
                val segmentWidth = maxWidth / items.size
                // 选中索引的弹簧动画：指示块随索引变化平滑滑动
                val animatedIndex by animateFloatAsState(
                    targetValue = selectedIndex.toFloat(),
                    animationSpec = spring(
                        dampingRatio = motion.overshootDamping,
                        stiffness = motion.overshootStiffness,
                    ),
                    label = "HamSegmentedThumb",
                )
                val thumbOffsetPx = segmentWidth.toPx() * animatedIndex
                Box(
                    Modifier
                        .offset(x = with(density) { thumbOffsetPx.toDp() })
                        .width(segmentWidth)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .background(
                            color = style.thumbColor ?: colors.accent,
                            shape = RoundedCornerShape(11.dp),
                        ),
                )
            }
            // 文字层：均匀分布，点击切换选中
            Row(Modifier.height(style.height)) {
                items.forEachIndexed { index, label ->
                    val selected = index == selectedIndex
                    val interaction = remember(index) { MutableInteractionSource() }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .hamPressable(interaction)
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                role = Role.Tab,
                                onClick = { onSegmentSelected(index) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = typography.label,
                            color = if (selected) colors.onAccent else colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
