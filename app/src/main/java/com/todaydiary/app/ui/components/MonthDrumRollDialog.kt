package com.todaydiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.todaydiary.app.ui.responsiveSp
import java.time.YearMonth

@Composable
fun MonthDrumRollDialog(
    initial: YearMonth,
    yearRange: IntRange,
    onDismiss: () -> Unit,
    onPicked: (YearMonth) -> Unit,
) {
    val years = remember(yearRange) { yearRange.toList() }
    val months = remember { (1..12).toList() }

    var selectedYearIndex by remember(initial, yearRange) {
        mutableIntStateOf((initial.year - yearRange.first).coerceIn(0, years.lastIndex))
    }
    var selectedMonthIndex by remember(initial) {
        mutableIntStateOf((initial.monthValue - 1).coerceIn(0, 11))
    }

    // 드래그 중에도 최신값을 유지하고, 닫힐 때/변경될 때 콜백
    LaunchedEffect(selectedYearIndex, selectedMonthIndex) {
        onPicked(YearMonth.of(years[selectedYearIndex], months[selectedMonthIndex]))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .background(Color(0x99000000)) // #000000 60%
                .fillMaxSize()
                .noIndicationClickable(onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(188.dp)
                    .height(144.dp),
            ) {
                Row(
                    modifier = Modifier
                        .width(152.dp)
                        .height(108.dp)
                        .padding(0.dp)
                        .wrapContentSize(align = Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    WheelPicker(
                        items = years,
                        selectedIndex = selectedYearIndex,
                        onSelectedIndexChange = { selectedYearIndex = it },
                        modifier = Modifier
                            .width(70.dp)
                            .height(72.dp),
                        itemHeight = 36.dp,
                        visibleCount = 2,
                        textStyle = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 15.responsiveSp(),
                        ),
                        label = { "${it}년" },
                        selectedOpacity = 1f,
                        unselectedOpacity = 0.2f,
                    )

                    WheelPicker(
                        items = months,
                        selectedIndex = selectedMonthIndex,
                        onSelectedIndexChange = { selectedMonthIndex = it },
                        modifier = Modifier
                            .width(70.dp)
                            .height(108.dp),
                        itemHeight = 36.dp,
                        visibleCount = 3,
                        textStyle = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 15.responsiveSp(),
                        ),
                        label = { "${it}월" },
                        selectedOpacity = 1f,
                        unselectedOpacity = 0.2f,
                    )
                }
            }
        }
    }
}

