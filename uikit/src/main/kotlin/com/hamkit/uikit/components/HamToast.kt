package com.hamkit.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 提示浮层样式对象。
 */
@Immutable
data class HamToastStyle(
    /** 浮层文字色；null 自动取主题 */
    val textColor: Color? = null,
    /** 自动关闭时长（ms）；0 表示常驻不自动关闭 */
    val autoDismissMillis: Long = 2400,
)

/**
 * 玻璃提示浮层（Toast，扁平风）：厚玻璃胶囊，顶部滑入淡出。
 *
 * 通过 [visible] 受控显示隐藏；内容建议一行以内。
 *
 * @param visible 是否显示（受控）
 * @param text 提示文字
 * @param onDismiss 自动关闭 / 手动关闭后的回调（用于把 visible 置回 false）
 */
@Composable
fun HamToast(
    visible: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    glass: GlassSpec? = null,
    style: HamToastStyle = HamToastStyle(),
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    // 自动关闭：显示后计时归零触发回调
    LaunchedEffect(visible, style.autoDismissMillis) {
        if (visible && style.autoDismissMillis > 0) {
            kotlinx.coroutines.delay(style.autoDismissMillis)
            onDismiss()
        }
    }
    Box(
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(tween(220)) { -it } + fadeIn(tween(220)),
            exit = slideOutVertically(tween(180)) { -it } + fadeOut(tween(180)),
        ) {
            GlassSurface(
                spec = glass ?: GlassSpec.Thick,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = text,
                    style = typography.body,
                    color = style.textColor ?: colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                )
            }
        }
    }
}
