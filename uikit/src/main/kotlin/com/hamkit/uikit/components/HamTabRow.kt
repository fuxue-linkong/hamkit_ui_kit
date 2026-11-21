package com.hamkit.uikit.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.hamPressable
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamMotion
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 页签样式对象。
 */
@Immutable
data class HamTabRowStyle(
    /** 页签行高度 */
    val height: Dp = 44.dp,
    /** 指示条颜色；null 自动取强调色 */
    val indicatorColor: Color? = null,
)

/**
 * 玻璃页签行（扁平风）：底部强调色指示条随选中项滑动。
 *
 * @param items 页签文案
 * @param selectedIndex 当前选中索引（受控）
 * @param onTabSelected 选择回调
 */
@Composable
fun HamTabRow(
    items: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    style: HamTabRowStyle = HamTabRowStyle(),
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    val density = LocalDensity.current

    Column(modifier.fillMaxWidth()) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            // 指示条：垫在页签底部，选中索引经弹簧动画平滑滑动
            val tabWidth = maxWidth / items.size
            val motion = LocalHamMotion.current
            val animatedIndex by animateFloatAsState(
                targetValue = selectedIndex.toFloat(),
                animationSpec = spring(
                    dampingRatio = motion.overshootDamping,
                    stiffness = motion.overshootStiffness,
                ),
                label = "HamTabIndicator",
            )
            val indicatorOffsetPx = with(density) { tabWidth.toPx() } * animatedIndex
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = with(density) { indicatorOffsetPx.toDp() })
                    .width(tabWidth)
                    .height(3.dp)
                    .padding(horizontal = 14.dp)
                    .background(
                        color = style.indicatorColor ?: colors.accent,
                        shape = RoundedCornerShape(50),
                    ),
            )
            Row(Modifier.height(style.height)) {
                items.forEachIndexed { index, label ->
                    val selected = index == selectedIndex
                    val interaction = remember(index) { MutableInteractionSource() }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(style.height)
                            .hamPressable(interaction)
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                role = Role.Tab,
                                onClick = { onTabSelected(index) },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = if (selected) typography.button else typography.label,
                            color = if (selected) colors.accent else colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
