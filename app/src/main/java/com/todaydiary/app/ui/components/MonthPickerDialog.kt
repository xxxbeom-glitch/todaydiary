package com.todaydiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.todaydiary.app.ui.responsiveSp
import java.time.LocalDate

@Composable
fun MonthPickerDialog(
    initialMonth: LocalDate,
    yearRange: IntRange,
    onDismiss: () -> Unit,
    onMonthPicked: (year: Int, month: Int) -> Unit,
) {
    val cornerR = popupDp(16f)
    val pad = popupDp(24f)
    val yearW = popupDp(80f)
    val monthW = popupDp(50f)
    val wheelH = popupDp(128f)
    val spacerW = popupDp(59f)
    val wheelTextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = popupLabelSp(15).responsiveSp(),
    )

    val years = remember(yearRange) { yearRange.toList() }
    val months = remember { (1..12).toList() }

    var selectedYearIndex by remember(initialMonth, yearRange) {
        mutableIntStateOf((initialMonth.year - yearRange.first).coerceIn(0, years.lastIndex))
    }
    var selectedMonthIndex by remember(initialMonth) {
        mutableIntStateOf((initialMonth.monthValue - 1).coerceIn(0, 11))
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(cornerR)
                )
                .padding(pad)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WheelPicker(
                    items = years,
                    selectedIndex = selectedYearIndex,
                    onSelectedIndexChange = { selectedYearIndex = it },
                    modifier = Modifier
                        .width(yearW)
                        .height(wheelH),
                    textStyle = wheelTextStyle,
                    label = { "${it}년" },
                )

                Spacer(modifier = Modifier.width(spacerW))

                WheelPicker(
                    items = months,
                    selectedIndex = selectedMonthIndex,
                    onSelectedIndexChange = { selectedMonthIndex = it },
                    modifier = Modifier
                        .width(monthW)
                        .height(wheelH),
                    textStyle = wheelTextStyle,
                    label = { "${it}월" },
                )
            }
        }
    }

    LaunchedEffect(selectedYearIndex, selectedMonthIndex) {
        onMonthPicked(years[selectedYearIndex], months[selectedMonthIndex])
    }
}
