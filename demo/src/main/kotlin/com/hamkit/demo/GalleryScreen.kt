package com.hamkit.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.components.GlassBackdrop
import com.hamkit.uikit.components.HamAvatar
import com.hamkit.uikit.components.HamBadge
import com.hamkit.uikit.components.HamBottomNavigation
import com.hamkit.uikit.components.HamButton
import com.hamkit.uikit.components.HamCard
import com.hamkit.uikit.components.HamCheckbox
import com.hamkit.uikit.components.HamChip
import com.hamkit.uikit.components.HamLinearProgress
import com.hamkit.uikit.components.HamListItem
import com.hamkit.uikit.components.HamRadioButton
import com.hamkit.uikit.components.HamSearchBar
import com.hamkit.uikit.components.HamSegmented
import com.hamkit.uikit.components.HamSkeleton
import com.hamkit.uikit.components.HamSwitch
import com.hamkit.uikit.components.HamTabRow
import com.hamkit.uikit.components.HamTextField
import com.hamkit.uikit.components.HamToast
import com.hamkit.uikit.components.HamTopAppBar
import com.hamkit.uikit.effect.hamEnter
import com.hamkit.uikit.theme.HamThemePreset
import com.hamkit.uikit.theme.LocalHamColors
import com.hamkit.uikit.theme.LocalHamTypography

/**
 * 组件画廊：所有 HamKit 组件的现场演示 + 玻璃参数实时调节。
 */
@Composable
fun GalleryScreen(
    isDark: Boolean,
    onToggleDark: () -> Unit,
    onPresetChange: (HamThemePreset) -> Unit,
) {
    val colors = LocalHamColors.current
    val typography = LocalHamTypography.current
    // 可调节的玻璃参数状态：直观展示"自定义底层"的效果
    var blurRadius by remember { mutableFloatStateOf(20f) }
    var surfaceAlpha by remember { mutableFloatStateOf(0.16f) }
    var inputText by remember { mutableStateOf("") }
    var switchOn by remember { mutableStateOf(true) }
    var sliderValue by remember { mutableFloatStateOf(0.6f) }
    var selectedChip by remember { mutableStateOf(0) }
    // v0.2 新组件演示状态
    var checkboxOn by remember { mutableStateOf(true) }
    var radioIndex by remember { mutableStateOf(0) }
    var segmentedIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var toastVisible by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        // 玻璃衬底：渐变背景衬托全部玻璃组件
        GlassBackdrop()
        Column(Modifier.fillMaxSize()) {
            HamTopAppBar(title = "HamKit Gallery")

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // ── 主题预设切换 ──
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HamThemePreset.entries.forEach { preset ->
                            HamChip(
                                text = preset.name,
                                selected = false,
                                onClick = { onPresetChange(preset) },
                            )
                        }
                        HamChip(text = if (isDark) "Light" else "Dark", selected = isDark, onClick = onToggleDark)
                    }
                }

                // ── 按钮 ──
                item {
                    HamCard(title = "Button", modifier = Modifier.hamEnter(index = 0)) {
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            HamButton(text = "玻璃按钮（填充宽度）", onClick = {}, glass = glassFrom(blurRadius, surfaceAlpha), style = com.hamkit.uikit.components.HamButtonStyle(fillMaxWidth = true))
                            HamButton(text = "强调按钮", onClick = {}, emphasized = true, style = com.hamkit.uikit.components.HamButtonStyle(fillMaxWidth = true))
                        }
                    }
                }

                // ── 输入 ──
                item {
                    HamCard(title = "TextField", modifier = Modifier.hamEnter(index = 1)) {
                        Spacer(Modifier.height(12.dp))
                        HamTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            hint = "输入点什么…",
                            glass = glassFrom(blurRadius, surfaceAlpha),
                        )
                    }
                }

                // ── 开关与滑杆 ──
                item {
                    HamCard(title = "Switch & Slider", modifier = Modifier.hamEnter(index = 2)) {
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HamSwitch(checked = switchOn, onCheckedChange = { switchOn = it }, glass = glassFrom(blurRadius, surfaceAlpha))
                            Spacer(Modifier.padding(6.dp))
                            androidx.compose.material3.Text("玻璃开关", style = typography.body, color = colors.textPrimary)
                        }
                        Spacer(Modifier.height(14.dp))
                        HamSlider(value = sliderValue, onValueChange = { sliderValue = it }, glass = glassFrom(blurRadius, surfaceAlpha))
                        Spacer(Modifier.height(10.dp))
                        HamLinearProgress(progress = sliderValue)
                    }
                }

                // ── v0.2 新组件：选择类 ──
                item {
                    HamCard(title = "Checkbox / Radio / Segmented", modifier = Modifier.hamEnter(index = 3)) {
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            HamCheckbox(checked = checkboxOn, onCheckedChange = { checkboxOn = it })
                            HamRadioButton(selected = radioIndex == 0, onClick = { radioIndex = 0 })
                            HamRadioButton(selected = radioIndex == 1, onClick = { radioIndex = 1 })
                        }
                        Spacer(Modifier.height(14.dp))
                        HamSegmented(
                            items = listOf("日", "周", "月"),
                            selectedIndex = segmentedIndex,
                            onSegmentSelected = { segmentedIndex = it },
                        )
                    }
                }

                // ── v0.2 新组件：信息与列表 ──
                item {
                    HamCard(title = "Search / Avatar / ListItem / Skeleton", modifier = Modifier.hamEnter(index = 4)) {
                        Spacer(Modifier.height(12.dp))
                        HamSearchBar(value = searchQuery, onValueChange = { searchQuery = it })
                        Spacer(Modifier.height(12.dp))
                        HamListItem(
                            title = "玻璃列表行",
                            subtitle = "leading / content / trailing 三槽位",
                            leading = { HamAvatar(label = "H") },
                            trailing = { HamBadge(text = "9") },
                            onClick = { toastVisible = true },
                        )
                        Spacer(Modifier.height(12.dp))
                        HamSkeleton(widthFraction = 0.92f)
                        Spacer(Modifier.height(8.dp))
                        HamSkeleton(widthFraction = 0.68f)
                    }
                }

                // ── v0.2 新组件：导航与浮层 ──
                item {
                    HamCard(title = "TabRow / Fab / Toast", modifier = Modifier.hamEnter(index = 5)) {
                        Spacer(Modifier.height(12.dp))
                        HamTabRow(items = listOf("推荐", "关注", "热榜"), selectedIndex = segmentedIndex, onTabSelected = { segmentedIndex = it })
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            HamFab(onClick = { toastVisible = true }) {
                                androidx.compose.material3.Text(
                                    "+",
                                    style = typography.title,
                                    color = colors.onAccent,
                                )
                            }
                            HamButton(text = "弹出 Toast", onClick = { toastVisible = true })
                        }
                    }
                }

                // ── 玻璃参数实时调节（展示自定义底层） ──
                item {
                    HamCard(title = "Glass Tuner（实时调节底层参数）", modifier = Modifier.hamEnter(index = 6)) {
                        Spacer(Modifier.height(10.dp))
                        androidx.compose.material3.Text(
                            "blurRadius = ${blurRadius.toInt()}dp",
                            style = typography.caption,
                            color = colors.textSecondary,
                        )
                        HamSlider(value = blurRadius / 40f, onValueChange = { blurRadius = it * 40f })
                        Spacer(Modifier.height(12.dp))
                        androidx.compose.material3.Text(
                            "surfaceAlpha = ${"%.2f".format(surfaceAlpha)}",
                            style = typography.caption,
                            color = colors.textSecondary,
                        )
                        HamSlider(value = surfaceAlpha, onValueChange = { surfaceAlpha = it })
                        Spacer(Modifier.height(12.dp))
                        HamButton(
                            text = "用当前参数刷新卡片",
                            onClick = {},
                            glass = glassFrom(blurRadius, surfaceAlpha),
                            style = com.hamkit.uikit.components.HamButtonStyle(fillMaxWidth = true),
                        )
                    }
                }
            }

            // ── 底部导航 ──
            HamBottomNavigation(
                items = listOf("首页", "组件", "关于"),
                selectedIndex = 0,
                onItemSelected = {},
            )
        }
        // 玻璃 Toast：点击列表行 / FAB 触发
        HamToast(visible = toastVisible, text = "HamKit v0.2：交互与动效已就绪 🐹", onDismiss = { toastVisible = false })
    }
}

/** 由两个实时参数组装玻璃规范（演示用便捷函数） */
private fun glassFrom(blurRadius: Float, surfaceAlpha: Float) =
    com.hamkit.uikit.theme.GlassSpec(
        blurRadius = androidx.compose.ui.unit.Dp(blurRadius),
        surfaceAlpha = surfaceAlpha,
    )
