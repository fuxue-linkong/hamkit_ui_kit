package com.hamkit.uikit.effect

import android.graphics.Bitmap
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.components.LocalBackdropLayer
import com.hamkit.uikit.theme.GlassSpec
import com.hamkit.uikit.theme.LocalHamColors
import kotlin.math.roundToInt
import kotlin.random.Random

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
 * 生成 64×64 单通道噪点位图（remember 缓存，每主题只生成一次）。
 *
 * 玻璃噪点用于打破大面积半透明的"塑料感"，属静态纹理，生成成本一次性。
 */
private fun createNoiseBitmap(): Bitmap {
    val size = 64
    val pixels = IntArray(size * size)
    val random = Random(2026)
    for (i in pixels.indices) {
        // 灰度随机噪点：亮度抖动 ±36
        val v = 128 + random.nextInt(-36, 36)
        pixels[i] = 0xFF shl 24 or (v shl 16) or (v shl 8) or v
    }
    return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
}

/**
 * HamKit 玻璃核心容器：所有玻璃组件的视觉基础（v0.4 重构）。
 *
 * 绘制层次（自底向上）：
 * 1. **透视模糊层**（API 31+）：绘制"玻璃背后"的背景快照（来自 GlassBackdrop 录制），
 *    经 `blur → brightness → saturation` 链式 RenderEffect 产生真实玻璃透视；
 * 2. **半透明表面**：玻璃质感主体；
 * 3. **噪点颗粒**：低透明度噪点平铺，打破塑料感；
 * 4. **内顶高光**：顶缘白色渐变光带，模拟玻璃顶缘反光；
 * 5. **边缘光描边**：上亮下暗渐变描边，勾勒玻璃体积。
 *
 * 降级规则（互斥）：
 * - 无背景快照可读 / blur 关闭 / API 26–30 → 不做透视模糊，
 *   表面加厚（alpha × 2.4）保证可读性，其余质感层照常绘制。
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
    // 噪点纹理：进程级缓存一次生成（android 位图直接用于 BitmapShader）
    val noiseBitmap = remember { createNoiseBitmap() }

    // 降级判定：不支持实时模糊时提高表面不透明度保证可读性
    val blurOn = resolveBlurEnabled(forceBlur)
    val backdropLayer = LocalBackdropLayer.current
    // 透视模糊成立条件：设备支持 + 模糊开启 + 有背景快照可读
    val canRefract = blurOn && backdropLayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val fallback = !blurOn
    val surfaceAlpha = when {
        // 完全降级（无模糊）：加厚表面保证可读性
        fallback -> (effectiveSpec.surfaceAlpha * 2.4f).coerceAtMost(0.75f)
        // 有透视模糊：表面可以更透，靠模糊层提供质感
        canRefract -> effectiveSpec.surfaceAlpha
        // 有模糊能力但无背景快照：维持原透明度
        else -> effectiveSpec.surfaceAlpha
    }

    val density = androidx.compose.ui.platform.LocalDensity.current
    // 模糊链构建放 Composable 作用域（provider.current 是 @Composable 调用），remember 缓存
    val blurProvider = LocalGlassBlurProvider.current
    val blurChain: AndroidRenderEffect? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remember(effectiveSpec.blurRadius, effectiveSpec.refraction, blurProvider) {
            val radiusPx = effectiveSpec.blurRadius.blurRadiusPx(density)
            val blur = blurProvider?.create(radiusPx)?.let { it as? AndroidRenderEffect }
                ?: AndroidRenderEffect.createBlurEffect(radiusPx, radiusPx, Shader.TileMode.CLAMP)
            var chain: AndroidRenderEffect = blur
            if (effectiveSpec.refraction > 0f) {
                // 折射：亮度与饱和度按 refraction 比例提升（iOS 玻璃"更亮更透"）
                val brightOffset = 30f * effectiveSpec.refraction
                val brightness = AndroidRenderEffect.createColorFilterEffect(
                    android.graphics.ColorMatrixColorFilter(
                        android.graphics.ColorMatrix(
                            floatArrayOf(
                                1f, 0f, 0f, 0f, brightOffset,
                                0f, 1f, 0f, 0f, brightOffset,
                                0f, 0f, 1f, 0f, brightOffset,
                                0f, 0f, 0f, 1f, 0f,
                            ),
                        ),
                    ),
                )
                chain = AndroidRenderEffect.createChainEffect(brightness, chain)
                val sat = 1f + 0.35f * effectiveSpec.refraction
                val saturate = AndroidRenderEffect.createColorFilterEffect(
                    android.graphics.ColorMatrixColorFilter(
                        android.graphics.ColorMatrix(
                            floatArrayOf(
                                sat, 0f, 0f, 0f, 0f,
                                0f, sat, 0f, 0f, 0f,
                                0f, 0f, sat, 0f, 0f,
                                0f, 0f, 0f, 1f, 0f,
                            ),
                        ),
                    ),
                )
                chain = AndroidRenderEffect.createChainEffect(saturate, chain)
            }
            chain
        }
    } else {
        null
    }
    // 玻璃自己的临时层：模糊只作用于"背景快照"，绝不波及子内容（文字）
    val scratchLayer = rememberGraphicsLayer()
    val blurModifier = if (blurOn) {
        Modifier
    } else {
        Modifier
    }

    Box(
        modifier
            .clip(effectiveShape)
            .then(blurModifier)
            // 玻璃主体绘制：背景快照（模糊）+ 表面色 + 噪点 + 高光 + 边缘光
            // 关键：模糊在 drawBehind 内部经 scratchLayer 应用，Box 的子内容（文字）
            // 不经过任何带 RenderEffect 的层，保持清晰
            .drawBehind {
                if (canRefract) {
                    // 1) 把"背后的画面"录进临时层并施加模糊链，再绘制
                    scratchLayer.record(
                        this,
                        layoutDirection,
                        IntSize(size.width.roundToInt(), size.height.roundToInt()),
                    ) { drawLayer(backdropLayer!!) }
                    scratchLayer.renderEffect = blurChain?.asComposeRenderEffect()
                    drawLayer(scratchLayer)
                    // 2) 表面色叠加在模糊背景上（玻璃白蒙层）
                    drawRect(surfaceColor.copy(alpha = surfaceAlpha))
                } else {
                    // 无透视时铺底色（表面层），保持原有观感
                    drawRect(surfaceColor.copy(alpha = surfaceAlpha))
                }
                // 2) 噪点颗粒：低透明度平铺，打破塑料感
                if (effectiveSpec.grain > 0f) {
                    drawIntoCanvas { canvas ->
                        val frameworkPaint = android.graphics.Paint()
                        frameworkPaint.shader = android.graphics.BitmapShader(
                            noiseBitmap,
                            Shader.TileMode.REPEAT,
                            Shader.TileMode.REPEAT,
                        )
                        frameworkPaint.alpha = (effectiveSpec.grain * 255).toInt()
                        canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, frameworkPaint)
                    }
                }
                // 3) 内顶高光：顶缘白色渐变光带（真实玻璃顶缘反光）
                if (effectiveSpec.highlight > 0f) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = effectiveSpec.highlight),
                                Color.Transparent,
                            ),
                            endY = size.height * 0.35f,
                        ),
                    )
                }
                // 4) 斜向微反光：玻璃面随角度的亮度差（扁平克制的 6% 强度）
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent,
                        ),
                        start = Offset(size.width * 0.15f, 0f),
                        end = Offset(size.width, size.height),
                    ),
                )
            }
            // 5) 边缘光：上亮下暗渐变描边（edgeGlow=0 回落纯色描边）
            .drawWithContent {
                drawContent()
                // 绘制描边（按 shape outline，宽度取 borderWidth）
                val stroke = effectiveSpec.borderWidth.toPx()
                if (effectiveSpec.edgeGlow > 0f) {
                    drawOutline(
                        effectiveShape.createOutline(size, layoutDirection, this),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = effectiveSpec.edgeGlow),
                                borderColor.copy(alpha = effectiveSpec.borderAlpha * 0.4f),
                            ),
                        ),
                        style = Stroke(width = stroke),
                    )
                } else {
                    drawOutline(
                        effectiveShape.createOutline(size, layoutDirection, this),
                        color = borderColor.copy(alpha = effectiveSpec.borderAlpha),
                        style = Stroke(width = stroke),
                    )
                }
            },
        content = content,
    )
}

/**
 * 便捷扩展：以 dp 换算像素的模糊半径提供给自定义 provider。
 */
internal fun Dp.blurRadiusPx(density: androidx.compose.ui.unit.Density): Float =
    with(density) { toPx() }
