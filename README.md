# HamKit UI Kit

Android 原生 **玻璃拟态（Glassmorphism）× 扁平风** Jetpack Compose UI 组件库。

- **性能优先**：Android 12+（API 31+）使用 `RenderEffect` 硬件实时模糊；Android 8.0~11（API 26–30）自动降级为高性能半透明玻璃（可注入自定义模糊实现）
- **零第三方依赖**：仅依赖 AndroidX / Compose 官方库
- **全量开放的自定义底层**：设计令牌、玻璃参数、主题、组件样式三级均可覆写
- `minSdk 26` / `targetSdk 37` / MIT 协议

[在线视觉演示页（HTML）](./docs/index.html) —— 浏览器直接打开即可预览全部组件的玻璃拟态效果（含明暗切换）。

## 快速开始

```kotlin
// 1) 一行启用主题（内置预设：Sky / Ocean / Sunset / Mono）
HamKitThemePreset(HamThemePreset.Sky, isDark = false) {
    // 2) 直接使用组件，玻璃参数自动继承主题
    HamButton(text = "确定", onClick = { /* ... */ }, emphasized = true)
    HamCard(title = "玻璃卡片") { /* ... */ }
}
```

## 三级自定义管线

自定义优先级：**组件实参 > 作用域覆盖（CompositionLocal）> 全局主题**，任何一层未提供自动向下一层回落。

### 第一级：全局主题（底层 Design Token）

```kotlin
HamKitTheme(
    colors = HamColors.Light.copy(accent = Color(0xFFFF6B35)),   // 整体换色
    glass = GlassSpec(blurRadius = 24.dp, surfaceAlpha = 0.12f), // 玻璃底层参数
) { /* ... */ }
```

### 第二级：作用域覆盖（子树生效）

```kotlin
GlassSpecOverride(spec = GlassSpec.Thin) {
    // 该子树内所有玻璃组件使用薄玻璃
    HamCard { ... }
}
```

### 第三级：组件实参（单点覆盖）

```kotlin
HamButton(text = "确定", onClick = { }, glass = GlassSpec.Thick)
HamSwitch(checked = on, onCheckedChange = { }, glass = GlassSpec(blurRadius = 8.dp))
```

## 底层可定制维度（`GlassSpec`）

| 参数 | 说明 | 扁平风推荐值 |
|---|---|---|
| `blurRadius` | 背景模糊半径（API 31+） | 16–28dp |
| `surfaceAlpha` | 表面不透明度 | 0.10–0.24 |
| `borderAlpha` / `borderWidth` | 半透明描边 | 0.22–0.34 / 1dp |
| `cornerRadius` | 圆角体系 | 16–24dp |
| `shadowElevation` | 阴影 | 0–2dp |
| `tint` | 玻璃染色 | null（用主题基色） |

此外 `HamColors`（含渐变底色组 `backdrop`）、`HamTypography`、`HamShapes` 全部可整体替换。

## 版本分级与扩展点

| API 级别 | 玻璃效果 |
|---|---|
| 31+ | `RenderEffect` 硬件实时模糊；Dialog/BottomSheet 走系统 Window blur |
| 26–30 | 半透明降级（性能优先）；需要真模糊时实现 `GlassBlurProvider` 注入，组件零改动 |

```kotlin
// 低端机强制降级（省电）
CompositionLocalProvider(LocalGlassBlurEnabled provides false) { ... }
```

## 组件清单（第一版）

GlassSurface（核心容器）、HamButton、HamCard、HamTextField、HamSwitch、HamSlider、HamChip、HamBadge、HamTopAppBar、HamAlertDialog、HamBottomSheet、HamBottomNavigation、HamLinearProgress、GlassBackdrop（渐变衬底）。

## 工程结构

```
uikit/   组件库（android library，com.hamkit.uikit）
demo/    示例 App（组件画廊 + 玻璃参数实时调节）
docs/    HTML 视觉演示页
```

## License

[MIT](./LICENSE)
