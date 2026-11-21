package com.hamkit.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * HamKit 字体体系（扁平风：层级靠字重与字号区分，不靠装饰）。
 *
 * 全部字段可替换：通过 [HamKitTheme] 传入自定义 [HamTypography]。
 */
@Immutable
data class HamTypography(
    /** 大标题 */
    val titleLarge: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    /** 页面标题 */
    val title: TextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    /** 卡片/分区标题 */
    val subtitle: TextStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    /** 正文 */
    val body: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 21.sp),
    /** 辅助说明 */
    val caption: TextStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    /** 按钮文字 */
    val button: TextStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    /** 标签 / 角标 */
    val label: TextStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
) {
    companion object {
        /** 默认字体族：跟随系统 */
        val Default: HamTypography = HamTypography()
    }
}
