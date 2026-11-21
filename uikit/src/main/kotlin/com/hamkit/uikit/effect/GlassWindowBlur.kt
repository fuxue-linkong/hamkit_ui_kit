package com.hamkit.uikit.effect

import android.app.Dialog
import android.os.Build
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

/**
 * 玻璃浮层的系统级模糊工具。
 *
 * Dialog / BottomSheet 这类独立窗口的模糊交给系统合成器（Window blur）处理，
 * 比自绘 RenderEffect 成本更低；仅 Android 12+ 支持，低版本静默降级为纯半透明。
 */
object GlassWindowBlur {

    /**
     * 为对话框窗口开启背景模糊。
     *
     * @param radius 背景模糊半径（px，系统接口以像素为单位）
     * @return 是否成功应用（低版本返回 false，调用方可据此调整浮层透明度）
     */
    fun applyTo(window: Window, radius: Int = 48): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
        // API 31+：设置窗口级背景模糊半径，由 SurfaceFlinger 合成时处理
        window.setBackgroundBlurRadius(radius)
        return true
    }

    /**
     * 便捷重载：从 Compose 环境取当前 Dialog 的 Window 并应用模糊。
     */
    @Composable
    fun rememberApply(radius: Int = 48): (Dialog) -> Boolean {
        // 记录当前 view 仅用于确保在 Compose 环境调用；实际设置作用于传入 window
        LocalView.current
        return { dialog -> applyTo(dialog.window!!, radius) }
    }
}
