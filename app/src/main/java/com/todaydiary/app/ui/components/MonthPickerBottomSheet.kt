package com.todaydiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todaydiary.app.ui.theme.DiaryBackground
import com.todaydiary.app.ui.theme.rememberDiaryFonts
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthPickerBottomSheet(
    initialMonth: LocalDate,
    yearRange: IntRange,
    onDismiss: () -> Unit,
    onMonthPicked: (year: Int, month: Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val fonts = rememberDiaryFonts()

    val pickerTextStyle = remember(fonts) {
        TextStyle(
            fontFamily = fonts.bold,
            fontSize = 20.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.3.sp, // 1.5%
        )
    }

    val years = remember(yearRange) { yearRange.toList() }
    val months = remember { (1..12).toList() }

    var selectedYearIndex by remember { mutableIntStateOf((initialMonth.year - yearRange.first).coerceIn(0, years.lastIndex)) }
    var selectedMonthIndex by remember { mutableIntStateOf((initialMonth.monthValue - 1).coerceIn(0, 11)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DiaryBackground,
        scrimColor = Color(0x66000000), // #000000 40%
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(4.dp)
                        .background(Color(0xFFD9D9D9), RoundedCornerShape(2.dp))
                )
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WheelPicker(
                items = years,
                selectedIndex = selectedYearIndex,
                onSelectedIndexChange = { selectedYearIndex = it },
                modifier = Modifier
                    .width(80.dp)
                    .height(128.dp),
                itemHeight = 32,
                itemSpacing = 16,
                visibleCount = 3,
                textStyle = pickerTextStyle,
                label = { "${it}년" },
            )

            Spacer(modifier = Modifier.width(59.dp))

            WheelPicker(
                items = months,
                selectedIndex = selectedMonthIndex,
                onSelectedIndexChange = { selectedMonthIndex = it },
                modifier = Modifier
                    .width(50.dp)
                    .height(128.dp),
                itemHeight = 32,
                itemSpacing = 16,
                visibleCount = 3,
                textStyle = pickerTextStyle,
                label = { "${it}월" },
            )
        }

        LaunchedEffect(selectedYearIndex, selectedMonthIndex) {
            onMonthPicked(years[selectedYearIndex], months[selectedMonthIndex])
        }
    }
}

