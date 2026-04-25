package com.todaydiary.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.theme.DiaryOnSurface
import com.todaydiary.app.ui.theme.DiaryBodyText
import com.todaydiary.app.ui.theme.rememberDiaryTextStyles

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
) {
    val textStyles = rememberDiaryTextStyles()
    val items = remember {
        listOf(
            "글자 크기",
            "폰트 설정",
            "테마 설정",
            "잠금 설정",
            "데이터 백업",
            "데이터 복원",
        )
    }
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(54.dp))
            DiaryTopBar(
                centerText = "설정",
                centerTextStyle = textStyles.listHeaderTitle,
                onBack = onBack,
            )

            // Figma: list block starts at y=197 relative to screen, header bottom is 82 => gap 115
            Spacer(modifier = Modifier.height(115.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items.forEachIndexed { idx, label ->
                    val isExpanded = expandedIndex == idx

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        val clickSource = remember { MutableInteractionSource() }
                        Text(
                            text = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(33.dp)
                                .clickable(
                                    interactionSource = clickSource,
                                    indication = null,
                                ) {
                                    expandedIndex = if (isExpanded) null else idx
                                },
                            color = DiaryOnSurface,
                            style = (if (isExpanded) textStyles.listItemDateSelected else textStyles.listItemDate)
                                .copy(textAlign = TextAlign.Center),
                        )

                        if (isExpanded) {
                            // 디자인 전 임시 확장 영역(추후 각 설정 UI로 교체)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "설정 준비중",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = DiaryBodyText,
                                style = textStyles.bodyInput,
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}

