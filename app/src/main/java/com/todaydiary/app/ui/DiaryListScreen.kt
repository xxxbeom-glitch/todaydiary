package com.todaydiary.app.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.MonthPickerBottomSheet
import com.todaydiary.app.ui.theme.DiaryOnSurface
import com.todaydiary.app.ui.theme.rememberDiaryTextStyles
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

@Composable
fun DiaryListScreen(
    month: LocalDate,
    entries: List<DiaryEntry>,
    onBack: () -> Unit = {},
    onSelectItem: (DiaryEntry) -> Unit = {},
    onPullDownToCompose: () -> Unit = {},
    onSettings: () -> Unit = {},
    onMonthChange: (LocalDate) -> Unit = {},
    yearRange: IntRange = (month.year - 1)..(month.year + 1),
) {
    val textStyles = rememberDiaryTextStyles()
    val monthTitle = remember(month) { "${month.monthValue}월" }
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val latestOnPullDownToCompose = rememberUpdatedState(onPullDownToCompose)
    val pullDownPx = remember { mutableFloatStateOf(0f) }
    val thresholdPx = remember(density) { with(density) { 80.dp.toPx() } }

    val pullDownConnection = remember(listState, thresholdPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val isAtTop = listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0

                if (source == NestedScrollSource.UserInput && isAtTop && available.y > 0f) {
                    pullDownPx.floatValue += available.y
                    // Consume vertical drag so the gesture is reliably tracked.
                    return Offset(x = 0f, y = available.y)
                }

                if (!isAtTop) pullDownPx.floatValue = 0f
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (pullDownPx.floatValue >= thresholdPx) {
                    latestOnPullDownToCompose.value.invoke()
                }
                pullDownPx.floatValue = 0f
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                pullDownPx.floatValue = 0f
                return super.onPostFling(consumed, available)
            }
        }
    }

    var isMonthSheetOpen by remember { mutableStateOf(false) }

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
            // Header: top 54px then 28px row (same as edit)
            Spacer(modifier = Modifier.height(54.dp))
            DiaryTopBar(
                centerText = "나의 일기",
                centerTextStyle = textStyles.listHeaderTitle,
                onBack = onBack,
                showBack = false,
                right = {
                    val clickSource = remember { MutableInteractionSource() }
                    Text(
                        text = "설정",
                        modifier = Modifier.clickable(
                            interactionSource = clickSource,
                            indication = null,
                            onClick = onSettings,
                        ),
                        color = DiaryOnSurface,
                        style = textStyles.headerDone,
                    )
                }
            )

            // Gap between header row and month title: 46px (y: 54+28 -> 82, month starts at 128)
            Spacer(modifier = Modifier.height(46.dp))

            Text(
                text = monthTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = textStyles.title,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { isMonthSheetOpen = true },
            )
            Spacer(modifier = Modifier.height(36.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullDownConnection),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                items(entries) { entry ->
                    val itemClickSource = remember { MutableInteractionSource() }
                    Text(
                        text = formatListItemDate(entry.date),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = itemClickSource,
                                indication = null,
                            ) { onSelectItem(entry) },
                        color = DiaryOnSurface,
                        style = textStyles.listItemDate,
                    )
                    Spacer(modifier = Modifier.height(29.dp))
                }
            }
        }
    }

    if (isMonthSheetOpen) {
        MonthPickerBottomSheet(
            initialMonth = month,
            yearRange = yearRange,
            onDismiss = { isMonthSheetOpen = false },
            onMonthPicked = { y, m ->
                onMonthChange(LocalDate.of(y, m, 1))
            },
        )
    }
}

