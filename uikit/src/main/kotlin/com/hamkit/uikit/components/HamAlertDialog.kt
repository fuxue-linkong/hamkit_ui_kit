package com.hamkit.uikit.components

import android.app.Dialog
import android.os.Build
import android.view.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hamkit.uikit.effect.GlassSurface
import com.hamkit.uikit.effect.GlassWindowBlur
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 对话框样式对象。
 */
@Immutable
data class HamDialogStyle(
    /** 确认按钮文字色；null 自动取强调色 */
    val confirmColor: Color? = null,
    /** 取消按钮文字色；null 自动取次级文字色 */
    val dismissColor: Color? = null,
)

/**
 * 玻璃对话框（扁平风）。
 *
 * Android 12+ 上对话框窗口自动应用系统级背景模糊（零自绘成本）；
 * 低版本自动降级为高不透明玻璃表面，保证可读性。
 *
 * @param onDismissRequest 点击外部/返回时的关闭回调
 * @param title 标题
 * @param confirmText 确认按钮文字；null 不显示
 * @param dismissText 取消按钮文字；null 不显示
 */
@Composable
fun HamAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    confirmText: String? = null,
    dismissText: String? = null,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    glass: GlassSpec? = null,
    style: HamDialogStyle = HamDialogStyle(),
    properties: DialogProperties = DialogProperties(),
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        // Android 12+ 为对话框窗口设置系统背景模糊
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocalView.current.context.findWindow()?.let { GlassWindowBlur.applyTo(it, radius = 56) }
        }
            // 低版本：加厚玻璃表面补偿不可模糊带来的层次缺失
            val fallbackSpec = GlassSpec.Thick.copy(surfaceAlpha = 0.42f)
            GlassSurface(
                modifier = modifier.fillMaxWidth(),
                spec = glass ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlassSpec.Thick else fallbackSpec,
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        style = typography.title,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = text,
                        style = typography.body,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    // 按钮区：取消居左、确认居右
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        dismissText?.let {
                            Text(
                                text = it,
                                style = typography.button,
                                color = style.dismissColor ?: colors.textSecondary,
                                modifier = Modifier
                                    .padding(end = 22.dp)
                                    .clickable(onClick = { onDismiss(); onDismissRequest() }),
                            )
                        }
                        confirmText?.let {
                            Text(
                                text = it,
                                style = typography.button,
                                color = style.confirmColor ?: colors.accent,
                                modifier = Modifier.clickable(onClick = { onConfirm(); onDismissRequest() }),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 从当前 View 上下文解析其所属 Window（对话框场景即 Dialog 窗口）。
 */
private tailrec fun android.view.View.findWindow(): Window? =
    when (val parent = parent) {
        is Dialog -> parent.window
        is android.view.ViewGroup -> parent.findWindow()
        else -> null
    }
