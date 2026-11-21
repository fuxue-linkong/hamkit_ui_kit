package com.hamkit.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hamkit.uikit.theme.HamKitThemePreset
import com.hamkit.uikit.theme.HamThemePreset

/**
 * Demo 入口：玻璃组件画廊 + 实时调节面板。
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 主题状态：预设与明暗切换均可在画廊内实时调整
            var preset by remember { mutableStateOf(HamThemePreset.Sky) }
            var isDark by remember { mutableStateOf(false) }
            HamKitThemePreset(preset = preset, isDark = isDark) {
                GalleryScreen(
                    isDark = isDark,
                    onToggleDark = { isDark = !isDark },
                    onPresetChange = { preset = it },
                )
            }
        }
    }
}
