package com.todaydiary.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R
import com.todaydiary.app.ui.components.DiaryListItem
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.OptionSelectDialog

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    isAccountLinked: Boolean,
    linkedEmail: String?,
    onClickLogin: () -> Unit = {},
    onClickLogout: () -> Unit = {},
) {
    val textBody = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.secondary

    val headerStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.responsiveSp(),
        fontWeight = FontWeight.Medium,
    )
    val titleStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.responsiveSp())
    val subtitleStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.responsiveSp())

    val screenOptions = remember { listOf("Light Mode", "Dark Mode") }
    val fontOptions = remember { listOf("폰트1", "폰트2", "폰트3") }
    var selectedScreenIndex by remember { mutableIntStateOf(0) }
    var selectedFontIndex by remember { mutableIntStateOf(0) }
    var showScreenDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiaryTopBar(
                left = {
                    IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                        // PNG 원본 그대로 (tint/크기 조절 없음)
                        Image(
                            painter = painterResource(id = R.drawable.ico_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp),
                        )
                    }
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
                preview = screenOptions[selectedScreenIndex],
                titleStyle = titleStyle,
                previewStyle = subtitleStyle,
                titleColor = textBody,
                previewColor = textSecondary,
                verticalPadding = 8.dp,
                textBlockVerticalPadding = 8.dp,
                previewWidth = 170.dp,
                showMore = false,
                onClick = { showScreenDialog = true },
            )

            // 3) 폰트 설정 (more 없음)
            DiaryListItem(
                title = "폰트 설정",
                preview = fontOptions[selectedFontIndex],
                titleStyle = titleStyle,
                previewStyle = subtitleStyle,
                titleColor = textBody,
                previewColor = textSecondary,
                verticalPadding = 8.dp,
                textBlockVerticalPadding = 8.dp,
                previewWidth = 170.dp,
                showMore = false,
                onClick = { showFontDialog = true },
            )

            // 4) 삭제된 일기 (more 없음)
            DiaryListItem(
                title = "삭제된 일기",
                preview = "총 4건",
                titleStyle = titleStyle,
                previewStyle = subtitleStyle,
                titleColor = textBody,
                previewColor = textSecondary,
                verticalPadding = 8.dp,
                textBlockVerticalPadding = 8.dp,
                previewWidth = 170.dp,
                showMore = false,
            )

            Spacer(modifier = Modifier.size(0.dp))
        }

        if (showScreenDialog) {
            OptionSelectDialog(
                options = screenOptions,
                selectedIndex = selectedScreenIndex,
                onDismiss = { showScreenDialog = false },
                onSelect = { selectedScreenIndex = it },
            )
        }

        if (showFontDialog) {
            OptionSelectDialog(
                options = fontOptions,
                selectedIndex = selectedFontIndex,
                onDismiss = { showFontDialog = false },
                onSelect = { selectedFontIndex = it },
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

