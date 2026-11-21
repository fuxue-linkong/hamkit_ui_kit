package com.hamkit.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 顶部栏高度（扁平风标准值）。
 */
val HamTopAppBarHeight: Dp = 56.dp

/**
 * 玻璃顶部栏（扁平风）：吸附在状态栏下方的悬浮玻璃条。
 *
 * 内容槽与 Material3 TopAppBar 对齐：navigationIcon / title / actions。
 *
 * @param title 标题文字
 * @param navigationIcon 导航区（返回键等）；建议放图标按钮
 * @param actions 动作区
 */
@Composable
fun HamTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    glass: GlassSpec? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        spec = glass ?: GlassSpec.Thin.copy(cornerRadius = 0.dp, borderAlpha = 0.16f),
        // 顶栏无圆角：与屏幕边缘齐平（扁平）
        shape = androidx.compose.ui.graphics.RectangleShape,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(HamTopAppBarHeight)
                .padding(horizontal = 8.dp),
        ) {
            // 导航区居左
            Box(Modifier.align(Alignment.CenterStart)) { navigationIcon() }
            // 标题居中
            Text(
                text = title,
                style = typography.title,
                color = colors.textPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
            // 动作区居右
            Row(
                Modifier.align(Alignment.CenterEnd),
                content = actions,
            )
        }
    }
}
