package com.todaydiary.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R
import com.todaydiary.app.ui.components.DiaryListItem
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.noIndicationClickable
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

@Composable
fun DiaryListScreen(
    entries: List<DiaryEntry> = sampleDiaryEntries(),
    monthTitle: String = "${LocalDate.now().year}년 ${LocalDate.now().monthValue}월",
    onClickMonth: () -> Unit = {},
    onClickCreate: () -> Unit = {},
    onClickHeaderLeft: () -> Unit = {},
    onClickEntryMore: (DiaryEntry) -> Unit = {},
    onClickEntry: (DiaryEntry) -> Unit = {},
) {
    val background = MaterialTheme.colorScheme.background
    val textBody = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.secondary

    val titleStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.responsiveSp(),
    )
    val dateStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 15.responsiveSp(),
    )
    val previewStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = 12.responsiveSp(),
    )

    Scaffold(
        containerColor = background,
        topBar = {
            DiaryTopBar(
                left = {
                    IconButton(
                        onClick = onClickHeaderLeft,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_setting),
                            contentDescription = "Settings",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                center = {
                    Row(
                        modifier = Modifier.noIndicationClickable(onClickMonth),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = monthTitle,
                            style = titleStyle,
                            color = textBody,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ico_dropdown),
                            contentDescription = "Month dropdown",
                            modifier = Modifier.size(10.dp),
                        )
                    }
                },
                right = {
                    IconButton(
                        onClick = onClickCreate,
                        modifier = Modifier.size(28.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_new),
                            contentDescription = "New",
                            modifier = Modifier.size(28.dp),
                        )
                    }
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
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(
                    items = entries,
                    key = { entry ->
                        if (entry.id.isNotBlank()) entry.id else "${entry.date}#${entry.body.hashCode()}"
                    },
                ) { entry ->
                    DiaryListItem(
                        title = formatListItemDate(entry.date),
                        preview = entry.body,
                        titleStyle = dateStyle,
                        previewStyle = previewStyle,
                        titleColor = textBody,
                        previewColor = textSecondary,
                        // 현재 화면 스펙 유지
                        verticalPadding = 8.dp,
                        textBlockVerticalPadding = 8.dp,
                        previewWidth = 220.dp,
                        onClick = { onClickEntry(entry) },
                        showMore = true,
                        onClickMore = { onClickEntryMore(entry) },
                    )
                }
            }
        }
    }
}

private fun sampleDiaryEntries(): List<DiaryEntry> {
    val body = "새벽안개가 걷히기도 전에 장화를 신고 온실로 향했다. 며칠 전부터"
    val today = LocalDate.now()
    return listOf(
        DiaryEntry(date = today, body = body),
        DiaryEntry(date = today.minusDays(1), body = body),
        DiaryEntry(date = today.minusDays(2), body = body),
        DiaryEntry(date = today.minusDays(3), body = body),
    )
}

