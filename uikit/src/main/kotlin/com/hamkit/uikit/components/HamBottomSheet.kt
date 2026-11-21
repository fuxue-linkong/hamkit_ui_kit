package com.hamkit.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 底部弹层样式对象。
 */
@Immutable
data class HamBottomSheetStyle(
    /** 标题颜色；null 自动取主题 */
    val titleColor: Color? = null,
    /** 拖动指示条颜色；null 自动取次级文字色 */
    val handleColor: Color? = null,
)

/**
 * 玻璃底部弹层（扁平风）：圆角玻璃面板 + 拖动指示条。
 *
 * 需要在 [androidx.compose.material3.ModalBottomSheet] 或自定义布局中承载使用。
 *
 * @param title 标题；传 null 不显示
 */
@Composable
fun HamBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    glass: GlassSpec? = null,
    shape: Shape? = null,
    style: HamBottomSheetStyle = HamBottomSheetStyle(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        spec = glass ?: GlassSpec.Thick,
        shape = shape,
    ) {
        Column(Modifier.padding(horizontal = 22.dp)) {
            // 拖动指示条：扁平风的轻量交互暗示
            Box(
                Modifier
                    .padding(top = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .background(
                        color = style.handleColor ?: colors.textSecondary.copy(alpha = 0.5f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    )
                    .align(Alignment.CenterHorizontally),
            )
            title?.let {
                Text(
                    text = it,
                    style = typography.subtitle,
                    color = style.titleColor ?: colors.textPrimary,
                    modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                )
            }
            content()
            Spacer(Modifier.height(14.dp))
        }
    }
}
