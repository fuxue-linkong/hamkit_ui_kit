package com.hamkit.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * HamKit 颜色体系。
 *
 * 玻璃拟态依赖"高饱和渐变底色 + 半透明表面"的衬托关系，
 * 因此这里除常规前景/强调色外，还提供 [backdrop] 渐变底色组。
 *
 * 全部字段可替换：通过 [HamKitTheme] 传入自定义 [HamColors] 即可整体换肤。
 */
@Immutable
data class HamColors(
    /** 页面渐变底色（2~3 个锚点色，玻璃在此之上折射） */
    val backdrop: List<Color>,
    /** 玻璃表面基色（浅色主题下为白、深色主题下为深灰蓝） */
    val glassSurface: Color,
    /** 玻璃描边基色 */
    val glassBorder: Color,
    /** 品牌强调色（按钮、选中态） */
    val accent: Color,
    /** 强调色之上的前景色 */
    val onAccent: Color,
    /** 一级文字 */
    val textPrimary: Color,
    /** 二级文字 */
    val textSecondary: Color,
    /** 危险色 */
    val danger: Color,
) {
    companion object {
        /** 浅色主题默认配色：清晨蓝色系 */
        val Light: HamColors = HamColors(
            backdrop = listOf(Color(0xFF8EC5FC), Color(0xFFE0C3FC)),
            glassSurface = Color.White,
            glassBorder = Color.White,
            accent = Color(0xFF3D6BFF),
            onAccent = Color.White,
            textPrimary = Color(0xFF17203D),
            textSecondary = Color(0xFF17203D).copy(alpha = 0.60f),
            danger = Color(0xFFE5484D),
        )

        /** 深色主题默认配色：午夜紫蓝系 */
        val Dark: HamColors = HamColors(
            backdrop = listOf(Color(0xFF0F1B45), Color(0xFF3B1F6E), Color(0xFF0C2A4D)),
            glassSurface = Color(0xFF101A33),
            glassBorder = Color(0xFFB9C8FF),
            accent = Color(0xFF7C9BFF),
            onAccent = Color(0xFF0B1030),
            textPrimary = Color(0xFFEDF1FF),
            textSecondary = Color(0xFFEDF1FF).copy(alpha = 0.62f),
            danger = Color(0xFFFF6B6E),
        )
    }
}
