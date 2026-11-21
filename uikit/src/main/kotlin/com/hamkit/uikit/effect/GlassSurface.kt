package com.hamkit.uikit.effect

import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamShapes

/**
 * 判断当前设备是否具备系统级实时模糊能力（API 31 引入 RenderEffect）。
 */
val isRuntimeBlurSupported: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * 解析最终生效的模糊开关：显式指定 > 设备能力自动判断。
 */
@Composable
fun resolveBlurEnabled(explicit: BlurPreference? = null): Boolean {
    // 显式指定优先（用户手动降级 / 强制开启），否则按设备能力自动
    val preference = explicit ?: LocalGlassBlurEnabled.current
    return preference ?: isRuntimeBlurSupported
}

/**
 * HamKit 玻璃核心容器：所有玻璃组件的视觉基础。
 *
 * 绘制三层结构（扁平玻璃拟态配方）：
 * 1. 半透明表面层 —— 玻璃质感主体；
 * 2. 实时模糊层 —— 仅 API 31+ 或注入了自定义 [GlassBlurProvider] 时生效；
 * 3. 半透明细描边 —— 勾勒玻璃边缘，扁平风格的关键细节。
 *
 * 性能设计：
 * - 模糊经 graphicsLayer 的 RenderEffect 作用于**自身 bounds** 的离屏层，
 *   不触发父级整屏重绘；
 * - 模糊半径与 alpha 参数以 remember 记忆，重组不重建 layer；
 * - 不支持模糊的设备自动降级为"更实的半透明 + 微渐变叠层"，绘制成本几乎为零。
 *
 * @param spec 玻璃规范；不传则读取 [LocalGlassSpec]（主题 / 作用域默认值）
 * @param shape 裁剪形状；不传则按 spec.cornerRadius 生成圆角
 * @param forceBlur 显式模糊开关（null 走全局策略）
 * @param modifier 布局修饰符
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    spec: GlassSpec? = null,
    shape: Shape? = null,
    forceBlur: BlurPreference = null,
    content: @Composable BoxScope.() -> Unit,
) {
    // 三级自定义管线：组件实参 > 作用域覆盖 > 全局主题默认
    val effectiveSpec = spec ?: LocalGlassSpec.current
    val colors = LocalHamColors.current
    val surfaceColor = effectiveSpec.tint ?: colors.glassSurface
    val borderColor = colors.glassBorder
    val effectiveShape = shape ?: remember(effectiveSpec.cornerRadius) {
        androidx.compose.foundation.shape.RoundedCornerShape(effectiveSpec.cornerRadius)
    }

    // 降级判定：不支持实时模糊时提高表面不透明度保证可读性
    val blurOn = resolveBlurEnabled(forceBlur)
    val fallback = !blurOn
    val surfaceAlpha = if (fallback) (effectiveSpec.surfaceAlpha * 2.4f).coerceAtMost(0.75f) else effectiveSpec.surfaceAlpha

    val blurModifier = if (blurOn) {
        Modifier.graphicsLayer {
            // 仅在设备支持且未提供自定义 provider 时走内置 RenderEffect 路径
            val provider = LocalGlassBlurProvider.current
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val radiusPx = effectiveSpec.blurRadius.blurRadiusPx(density)
                val effect = provider?.create(radiusPx)?.let { it as? AndroidRenderEffect }
                    ?: AndroidRenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP)
                renderEffect = effect.asComposeRenderEffect()
            }
            // 离屏层是 RenderEffect 生效前提
            compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
        }
    } else {
        Modifier
    }

    Box(
        modifier
            .clip(effectiveShape)
            .then(blurModifier)
            .background(surfaceColor.copy(alpha = surfaceAlpha), effectiveShape)
            .then(
                if (fallback) {
                    // 降级叠层：极淡的斜向渐变模拟玻璃反光，成本几乎为零
                    Modifier.drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.10f),
                                    Color.Transparent,
                                ),
                                start = Offset(size.width * 0.2f, 0f),
                                end = Offset(size.width, size.height),
                            ),
                        )
                    }
                } else {
                    Modifier
                },
            )
            .border(effectiveSpec.borderWidth, borderColor.copy(alpha = effectiveSpec.borderAlpha), effectiveShape),
        content = content,
    )
}

/**
 * 便捷扩展：以 dp 换算像素的模糊半径提供给自定义 provider。
 */
internal fun Dp.blurRadiusPx(density: androidx.compose.ui.unit.Density): Float =
    with(density) { toPx() }
