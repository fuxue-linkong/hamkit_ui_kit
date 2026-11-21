package com.hamkit.uikit.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hamkit.uikit.components.HamAvatar
import com.hamkit.uikit.components.HamBadge
import com.hamkit.uikit.components.HamBottomNavigation
import com.hamkit.uikit.components.HamButton
import com.hamkit.uikit.components.HamCard
import com.hamkit.uikit.components.HamCheckbox
import com.hamkit.uikit.components.HamChip
import com.hamkit.uikit.components.HamFab
import com.hamkit.uikit.components.HamListItem
import com.hamkit.uikit.components.HamRadioButton
import com.hamkit.uikit.components.HamSearchBar
import com.hamkit.uikit.components.HamSegmented
import com.hamkit.uikit.components.HamSkeleton
import com.hamkit.uikit.components.HamSwitch
import com.hamkit.uikit.components.HamTabRow
import com.hamkit.uikit.theme.HamKitThemePreset
import com.hamkit.uikit.theme.HamThemePreset

/**
 * Android Studio 预览：全部组件一览（浅色）。
 *
 * 在 IDE 中打开本文件即可不启动模拟器查看所有组件的真实 Compose 渲染。
 */
@Preview(name = "HamKit 全组件预览", showBackground = true, widthDp = 380, heightDp = 760)
@Composable
private fun AllComponentsPreview() {
    var checked by remember { mutableStateOf(true) }
    var segmented by remember { mutableStateOf(0) }
    var search by remember { mutableStateOf("") }

    HamKitThemePreset(HamThemePreset.Sky) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            // 选择类
            HamCheckbox(checked = checked, onCheckedChange = { checked = it })
            HamRadioButton(selected = true, onClick = {})
            HamSwitch(checked = checked, onCheckedChange = { checked = it })
            Spacer(Modifier.height(8.dp))
            HamSegmented(items = listOf("日", "周", "月"), selectedIndex = segmented, onSegmentSelected = { segmented = it })

            // 动作类
            Spacer(Modifier.height(8.dp))
            HamButton(text = "玻璃按钮", onClick = {})
            HamChip(text = "筛选", selected = true, onClick = {})

            // 信息类
            Spacer(Modifier.height(8.dp))
            HamSearchBar(value = search, onValueChange = { search = it })
            HamCard(title = "卡片标题") {
                Text("卡片正文示例文本。")
            }
            HamListItem(
                title = "列表行标题",
                subtitle = "副标题说明",
                leading = { HamAvatar(label = "H") },
                trailing = { HamBadge(text = "3") },
            )
            HamSkeleton(widthFraction = 0.9f)
            HamSkeleton(widthFraction = 0.7f)

            // 导航类
            Spacer(Modifier.height(8.dp))
            HamTabRow(items = listOf("推荐", "关注", "热榜"), selectedIndex = 0, onTabSelected = {})
            HamBottomNavigation(items = listOf("首页", "发现", "我的"), selectedIndex = 0, onItemSelected = {})
        }
    }
}

/**
 * FAB 独立预览。
 */
@Preview(name = "HamFab", showBackground = true)
@Composable
private fun FabPreview() {
    HamKitThemePreset(HamThemePreset.Ocean) {
        HamFab(onClick = {}) {
            Text("+", style = com.hamkit.uikit.theme.LocalHamTypography.current.title)
        }
    }
}
