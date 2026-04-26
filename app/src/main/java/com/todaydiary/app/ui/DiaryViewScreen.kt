package com.todaydiary.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

@Composable
fun DiaryViewScreen(
    entry: DiaryEntry,
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
) {
    val headerTextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.responsiveSp(),
        fontWeight = FontWeight.Medium,
    )
    val bodyTextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.responsiveSp(),
        lineHeight = 36.responsiveSp(),
    )

    val density = LocalDensity.current
    val lineHeightPx = with(density) { bodyTextStyle.lineHeight.toPx() }
    val firstLineY = with(density) { bodyTextStyle.fontSize.toPx() + 10.dp.toPx() }
    val lineColor = MaterialTheme.colorScheme.outline

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiaryTopBar(
                left = {
                    IconButton(onClick = onBack, modifier = Modifier.size(28.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                center = {
                    Text(
                        text = formatSubtitleDate(entry.date),
                        style = headerTextStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                right = {
                    IconButton(onClick = onMore, modifier = Modifier.size(28.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_more),
                            contentDescription = "More",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 헤더 바 아래 → 본문 시작 간격 (피그마: 28dp)
            // 에디터 페이지(헤더 바 아래 → 본문 시작 44dp)와 기준을 맞춥니다.
            // 여기서는 본문 Box에 vertical=10dp 패딩이 있으므로, Spacer를 34dp로 두면 34+10=44dp가 됩니다.
            Spacer(modifier = Modifier.height(34.dp))

            // 본문 영역이 화면 하단까지 꽉 차도록 weight(1f)로 고정
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val lineCount = (size.height / lineHeightPx).toInt() + 10
                    for (i in 0..lineCount) {
                        val y = firstLineY + (i * lineHeightPx)
                        drawLine(
                            color = lineColor,
                            start = Offset(x = 0f, y = y),
                            end = Offset(x = size.width, y = y),
                            strokeWidth = 1f,
                        )
                    }
                }

                Text(
                    text = entry.body,
                    style = bodyTextStyle,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                )
            }
        }
    }
}
