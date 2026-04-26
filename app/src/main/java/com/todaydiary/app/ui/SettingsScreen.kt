package com.todaydiary.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R
import com.todaydiary.app.ui.components.DiaryListItem
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.HeaderPngIconButton
import com.todaydiary.app.ui.components.FontSelectDialog
import com.todaydiary.app.ui.components.OptionSelectDialog
import com.todaydiary.app.ui.components.ThemedPngIcon

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    isAccountLinked: Boolean,
    linkedEmail: String?,
    onClickLogin: () -> Unit = {},
    onClickLogout: () -> Unit = {},
    screenThemeIndex: Int = 0,
    onScreenThemeIndexChange: (Int) -> Unit = {},
    fontIndex: Int = 0,
    onFontIndexChange: (Int) -> Unit = {},
) {
    val textBody = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.secondary

    val headerStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.responsiveSp(),
        fontWeight = FontWeight.Medium,
    )
    val titleStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.responsiveSp())
    val subtitleStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.responsiveSp())

    val screenOptions = remember { listOf("라이트 모드", "다크 모드") }
    val fontOptions = remember {
        listOf(
            "SUIT체",
            "바른바탕체",
            "카페24 고운밤체",
            "교보 이유빈체",
            "교보 박도연체",
        )
    }
    var showScreenDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiaryTopBar(
                left = {
                    HeaderPngIconButton(
                        onClick = onBack,
                        resId = R.drawable.ico_back,
                        contentDescription = "Back",
                    )
                },
                center = {
                    Text(
                        text = "설정",
                        style = headerStyle,
                        color = textBody,
                    )
                },
                right = {
                    // Figma: 우측은 비어있는 28dp 영역
                    Box(modifier = Modifier.size(28.dp))
                },
            )
        },
    ) { paddingValues ->
        // Figma: list starts at y=112dp; topBar bottom is 36+48=84 → gap = 28dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // 1) 계정 (우측 more 있음)
            DiaryListItem(
                title = if (isAccountLinked) "구글 계정으로 로그인 됨" else "구글 계정으로 로그인",
                preview = if (isAccountLinked) (linkedEmail ?: "") else "연결된 계정이 없습니다",
                titleStyle = titleStyle,
                previewStyle = subtitleStyle,
                titleColor = textBody,
                previewColor = textSecondary,
                previewMaxLines = 1,
                verticalPadding = 8.dp,
                textBlockVerticalPadding = 8.dp,
                previewWidth = 170.dp,
                onClick = {
                    if (isAccountLinked) showLogoutDialog = true else onClickLogin()
                },
                showMore = true,
                moreIconResId = R.drawable.ico_more,
                moreContentDescription = "Account more",
                onClickMore = {
                    if (isAccountLinked) {
                        showLogoutDialog = true
                    } else {
                        onClickLogin()
                    }
                },
            )

            // 2) 화면 설정 (more 없음)
            DiaryListItem(
                title = "화면 설정",
                preview = screenOptions[screenThemeIndex.coerceIn(0, screenOptions.lastIndex)],
                titleStyle = titleStyle,
                previewStyle = subtitleStyle,
                titleColor = textBody,
                previewColor = textSecondary,
                previewMaxLines = 1,
                verticalPadding = 8.dp,
                textBlockVerticalPadding = 8.dp,
                previewWidth = 170.dp,
                showMore = false,
                onClick = { showScreenDialog = true },
            )

            // 3) 폰트 설정 (more 없음)
            DiaryListItem(
                title = "폰트 설정",
                preview = fontOptions[fontIndex.coerceIn(0, fontOptions.lastIndex)],
                titleStyle = titleStyle,
                previewStyle = subtitleStyle,
                titleColor = textBody,
                previewColor = textSecondary,
                previewMaxLines = 1,
                verticalPadding = 8.dp,
                textBlockVerticalPadding = 8.dp,
                previewWidth = 170.dp,
                showMore = false,
                onClick = { showFontDialog = true },
            )

            Spacer(modifier = Modifier.size(0.dp))
        }

        if (showScreenDialog) {
            OptionSelectDialog(
                options = screenOptions,
                selectedIndex = screenThemeIndex.coerceIn(0, screenOptions.lastIndex),
                onDismiss = { showScreenDialog = false },
                onSelect = { index ->
                    onScreenThemeIndexChange(index)
                },
            )
        }

        if (showFontDialog) {
            FontSelectDialog(
                options = fontOptions,
                selectedIndex = fontIndex.coerceIn(0, fontOptions.lastIndex),
                onDismiss = { showFontDialog = false },
                onSelect = { onFontIndexChange(it) },
            )
        }

        if (showLogoutDialog) {
            OptionSelectDialog(
                options = listOf("로그아웃"),
                selectedIndex = 0,
                onDismiss = { showLogoutDialog = false },
                onSelect = {
                    onClickLogout()
                },
            )
        }
    }
}

