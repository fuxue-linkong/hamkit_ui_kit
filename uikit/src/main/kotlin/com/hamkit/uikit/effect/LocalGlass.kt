package com.hamkit.uikit.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.hamkit.uikit.theme.GlassSpec

/**
 * 是否允许实时背景模糊。
 *
 * - `null`：自动策略 —— API 31+ 开启硬件模糊，API 26–30 使用半透明降级（性能优先）；
 * - `true`：强制开启（低版本设备需配合 [LocalGlassBlurProvider] 注入自定义实现）；
 * - `false`：强制关闭，例如低端机、省电模式下主动降级。
 */
typealias BlurPreference = Boolean?

/**
 * 自定义模糊实现扩展点。
 *
 * 默认策略在 API 31+ 使用系统 [androidx.compose.ui.graphics.RenderEffect]；
 * 若厂商希望在低版本设备获得真实模糊（如自研快照模糊），可实现本接口并通过
 * [LocalGlassBlurProvider] 注入，所有玻璃组件零改动生效。
 *
 * 返回值约定：API 31+ 运行环境下可返回 RenderEffect 实例（由框架内部转换消费），
 * 返回 null 表示本次无法模糊、走降级绘制。
 */
fun interface GlassBlurProvider {
    /**
     * @param radiusPx 模糊半径（像素）
     * @return 可被框架消费的模糊效果；null 表示降级
     */
    fun create(radiusPx: Float): Any?
}

/**
 * CompositionLocal 管线：所有玻璃组件的默认值均从这里读取，
 * 形成"组件实参 > 作用域覆盖 > 全局主题"三级自定义次序。
 */
object GlassDefaults {
    /** 当前生效的玻璃规范 */
    val glassSpec: ProvidableCompositionLocal<GlassSpec> = staticCompositionLocalOf { GlassSpec.Defaults }

    /** 模糊开关（null = 按设备 API 自动） */
    val blurEnabled: ProvidableCompositionLocal<BlurPreference> = staticCompositionLocalOf { null }

    /** 自定义模糊实现（默认 null = 使用框架内置策略） */
    val blurProvider: ProvidableCompositionLocal<GlassBlurProvider?> = staticCompositionLocalOf { null }
}

/**
 * 子树级玻璃规范覆盖。
 *
 * 典型用法：让某一块界面整体更透、模糊更强，而无需逐组件传参：
 *
 * ```
 * GlassSpecOverride(spec = GlassSpec.Thin) {
 *     HamCard { ... }
 * }
 * ```
 */
@Composable
fun GlassSpecOverride(
    spec: GlassSpec,
    blurEnabled: BlurPreference = null,
    blurProvider: GlassBlurProvider? = null,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        GlassDefaults.glassSpec provides spec,
        GlassDefaults.blurEnabled provides blurEnabled,
        GlassDefaults.blurProvider provides blurProvider ?: LocalGlassBlurProvider.current,
        content = content,
    )
}

/** 当前 Compose 树中的玻璃规范快捷读取 */
val LocalGlassSpec: ProvidableCompositionLocal<GlassSpec> get() = GlassDefaults.glassSpec

/** 当前模糊开关快捷读取 */
val LocalGlassBlurEnabled: ProvidableCompositionLocal<BlurPreference> get() = GlassDefaults.blurEnabled

/** 当前自定义模糊实现快捷读取 */
val LocalGlassBlurProvider: ProvidableCompositionLocal<GlassBlurProvider?> get() = GlassDefaults.blurProvider

/** 玻璃覆盖作用域描述（内部使用，记录覆盖是否显式提供） */
@Immutable
internal data class GlassOverride(
    val spec: GlassSpec?,
    val blurEnabled: BlurPreference,
    val hasBlurProvider: Boolean,
)
