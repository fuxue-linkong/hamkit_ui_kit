package com.hamkit.uikit.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * HamKit 形状体系（扁平风：统一的中等圆角，不做异形切割）。
 *
 * 全部字段可替换：通过 [HamKitTheme] 传入自定义 [HamShapes]。
 */
@Immutable
data class HamShapes(
    /** 小元素：徽标、进度条 */
    val small: Shape = RoundedCornerShape(8.dp),
    /** 中元素：输入框、Chip */
    val medium: Shape = RoundedCornerShape(14.dp),
    /** 大元素：卡片、按钮 */
    val large: Shape = RoundedCornerShape(20.dp),
    /** 药丸形：开关、标签 */
    val full: Shape = RoundedCornerShape(50),
) {
    companion object {
        val Default: HamShapes = HamShapes()
    }
}
