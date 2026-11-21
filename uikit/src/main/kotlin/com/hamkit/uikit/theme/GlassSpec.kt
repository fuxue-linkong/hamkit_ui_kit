package com.hamkit.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 玻璃效果底层规范（Design Token）。
 *
 * 这是 HamKit 玻璃拟态体系的最底层可定制单元：主题、组件、单次调用
 * 三级自定义最终都归结为对 [GlassSpec] 的调整。
 *
 * @param blurRadius     背景模糊半径，仅在支持实时模糊的设备（API 31+）生效
 * @param surfaceAlpha   玻璃表面不透明度（0f 全透明 ~ 1f 不透明），扁平风推荐 0.10~0.20
 * @param borderAlpha    半透明描边不透明度，用于勾勒玻璃边缘
 * @param borderWidth    描边宽度，扁平风推荐 1dp 细线
 * @param cornerRadius   默认圆角半径，可被组件的 shape 参数覆盖
 * @param shadowElevation 阴影高度，扁平风默认 0~2dp 轻柔阴影
 * @param tint           玻璃染色色，null 表示使用主题玻璃基色
 */
@Immutable
data class GlassSpec(
    val blurRadius: Dp = 20.dp,
    val surfaceAlpha: Float = 0.16f,
    val borderAlpha: Float = 0.28f,
    val borderWidth: Dp = 1.dp,
    val cornerRadius: Dp = 20.dp,
    val shadowElevation: Dp = 0.dp,
    val tint: Color? = null,
) {
    companion object {
        /** 默认规范：常规玻璃厚度 */
        val Defaults: GlassSpec = GlassSpec()

        /** 薄玻璃：更透、更轻，适合大面积卡片 */
        val Thin: GlassSpec = GlassSpec(blurRadius = 16.dp, surfaceAlpha = 0.10f, borderAlpha = 0.22f)

        /** 厚玻璃：更实、更醒目，适合浮层 / 对话框 */
        val Thick: GlassSpec = GlassSpec(blurRadius = 28.dp, surfaceAlpha = 0.24f, borderAlpha = 0.34f, shadowElevation = 2.dp)

        /** 扁平极简：几乎无模糊与阴影，仅保留玻璃描边质感 */
        val Flat: GlassSpec = GlassSpec(blurRadius = 8.dp, surfaceAlpha = 0.12f, borderAlpha = 0.20f, shadowElevation = 0.dp)
    }
}
