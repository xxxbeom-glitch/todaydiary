package com.todaydiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.todaydiary.app.ui.responsiveSp
import kotlin.math.abs
import java.time.YearMonth

/**
 * 달 드럼롤. [openSession]은 **다이얼로그를 열 때마다**만 바뀌어야 함. (같은 세션 동안 [availableMonths]가
 * Firestore 등으로 바뀌어도 휠이 `initial`+`years` [remember]로 다시 초기화되지 않게 함)
 */
@Composable
fun MonthDrumRollDialog(
    openSession: Int,
    initial: YearMonth,
    availableMonths: List<YearMonth>,
    onDismiss: () -> Unit,
    onPicked: (YearMonth) -> Unit,
) {
    val surfaceW = popupDp(188f)
    val surfaceH = popupDp(144f)
    val cornerR = popupDp(12f)
    val wheelHeight = popupDp(108f)
    val itemH = popupDp(36f)
    val rowW = popupDp(152f)
    val colW = popupDp(70f)
    val wheelGap = popupDp(12f)
    val labelSp = popupLabelSp(15)

    val sortedRaw = remember(availableMonths) { availableMonths.distinct().sorted() }
    val sortedMonths = if (sortedRaw.isNotEmpty()) sortedRaw else listOf(initial)
    val years = remember(sortedMonths) { sortedMonths.map { it.year }.distinct().sorted() }

    var picked by remember(openSession) { mutableStateOf(initial) }

    LaunchedEffect(sortedMonths, picked) {
        if (sortedMonths.isEmpty()) return@LaunchedEffect
        if (picked in sortedMonths) return@LaunchedEffect
        val nearest = sortedMonths.minByOrNull { ym ->
            val a = ym.year * 12L + ym.monthValue
            val b = picked.year * 12L + picked.monthValue
            abs(a - b)
        } ?: return@LaunchedEffect
        picked = nearest
    }

    val yIdx0 = years.indexOf(picked.year)
    val yearIndex = (if (yIdx0 >= 0) yIdx0 else 0).coerceIn(0, (years.size - 1).coerceAtLeast(0))
    val y = years.getOrNull(yearIndex) ?: picked.year
    val monthsInYear = remember(sortedMonths, y) {
        sortedMonths
            .filter { it.year == y }
            .map { it.monthValue }
            .distinct()
            .sorted()
    }
    val mIdx0 = monthsInYear.indexOf(picked.monthValue)
    val monthIndex = if (monthsInYear.isEmpty()) {
        0
    } else {
        (if (mIdx0 >= 0) mIdx0 else 0).coerceIn(0, monthsInYear.lastIndex)
    }

    val commitAndDismiss = {
        onPicked(picked)
        onDismiss()
    }

    Dialog(
        onDismissRequest = commitAndDismiss,
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
                .noIndicationClickable(commitAndDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(cornerR),
                modifier = Modifier
                    .width(surfaceW)
                    .height(surfaceH),
            ) {
                val vCount = 3
                if (years.isNotEmpty() && monthsInYear.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            modifier = Modifier
                                .width(rowW)
                                .height(wheelHeight),
                            horizontalArrangement = Arrangement.spacedBy(wheelGap),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            WheelPicker(
                                items = years,
                                selectedIndex = yearIndex,
                                onSelectedIndexChange = { newYearIdx ->
                                    if (newYearIdx !in years.indices) return@WheelPicker
                                    val yNew = years[newYearIdx]
                                    val mNums = sortedMonths
                                        .filter { it.year == yNew }
                                        .map { it.monthValue }
                                        .distinct()
                                        .sorted()
                                    if (mNums.isEmpty()) return@WheelPicker
                                    val m = when {
                                        picked.monthValue in mNums -> picked.monthValue
                                        else -> mNums.minBy { abs(it - picked.monthValue) }
                                    }
                                    picked = YearMonth.of(yNew, m)
                                },
                                modifier = Modifier
                                    .width(colW)
                                    .height(wheelHeight),
                                itemHeight = itemH,
                                visibleCount = vCount,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = labelSp.responsiveSp(),
                                ),
                                label = { "${it}년" },
                                selectedOpacity = 1f,
                                unselectedOpacity = 0.2f,
                            )

                            WheelPicker(
                                items = monthsInYear,
                                selectedIndex = monthIndex,
                                onSelectedIndexChange = { newMonthIdx ->
                                    if (newMonthIdx !in monthsInYear.indices) return@WheelPicker
                                    if (yearIndex !in years.indices) return@WheelPicker
                                    val yCur = years[yearIndex]
                                    val m = monthsInYear[newMonthIdx]
                                    picked = YearMonth.of(yCur, m)
                                },
                                modifier = Modifier
                                    .width(colW)
                                    .height(wheelHeight),
                                itemHeight = itemH,
                                visibleCount = vCount,
                                textStyle = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = labelSp.responsiveSp(),
                                ),
                                label = { "${it}월" },
                                selectedOpacity = 1f,
                                unselectedOpacity = 0.2f,
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
