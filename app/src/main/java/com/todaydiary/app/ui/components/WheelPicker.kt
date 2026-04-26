package com.todaydiary.app.ui.components

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.delay
import kotlin.math.abs
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 패딩(null) 행이 뷰포트 중심에 가까우면 논리 인덱스가 -1이 되어 스냝 대기가 끝나지 않는 문제가 있어, 데이터 행만 후보로 쓴다. */
private fun centeredItemIndexInLazyList(
    listState: LazyListState,
    halfVisibleCount: Int,
    itemCount: Int,
): Int {
    val layoutInfo = listState.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo
    if (visibleItemsInfo.isEmpty() || itemCount <= 0) return -1
    val viewportCenter = layoutInfo.viewportEndOffset / 2
    val dataRows = visibleItemsInfo.filter { info ->
        val logical = info.index - halfVisibleCount
        logical in 0 until itemCount
    }
    if (dataRows.isEmpty()) return -1
    val centerItem = dataRows.minByOrNull { itemInfo ->
        abs((itemInfo.offset + itemInfo.size / 2) - viewportCenter)
    }
    return centerItem?.index?.minus(halfVisibleCount) ?: -1
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    items: List<T>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp,
    visibleCount: Int = 3,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    label: (T) -> String,
    selectedOpacity: Float = 1f,
    unselectedOpacity: Float = 1f,
) {
    val latestOnSelectedIndexChange = rememberUpdatedState(onSelectedIndexChange)
    val latestSelectedIndex = rememberUpdatedState(selectedIndex)
    val view: View = LocalView.current

    val halfVisibleCount = visibleCount / 2
    val paddedItems = remember(items, halfVisibleCount) {
        val padding = List(halfVisibleCount) { null }
        padding + items + padding
    }

    // 기본 firstVisible=0 → 열릴 때 1(월)·… 맨 앞이 보이다가 scrollToItem으로
    // 3·4·5·…를 지나가는 것처럼 보이고(늦은 반응), 그동안 콜백도 연쇄로 나갈 수 있음.
    // scrollToItem과 맞는 padded 인덱스로 초기 스크롤을 둬서 바로 그 줄이 중심에 오게 한다.
    val initialPaddedItemIndex = remember(paddedItems.size, items, selectedIndex) {
        if (items.isEmpty() || selectedIndex !in items.indices) 0
        else (selectedIndex + halfVisibleCount).coerceIn(0, (paddedItems.size - 1).coerceAtLeast(0))
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialPaddedItemIndex,
        initialFirstVisibleItemScrollOffset = 0,
    )

    /** 터치(드래그) 후에만 중심→[onSelectedIndexChange]. 초기 layout·[scrollToItem] 직후 오차로 한 칸(한 달) 밀림 방지 */
    val userInteracted = remember { mutableStateOf(false) }

    // 사용자가 드래그로 맞춘 뒤: 뷰포트 중심이 이미 selectedIndex이면 scrollToItem 생략
    var canApplyCenteredSelection by remember { mutableStateOf(false) }
    LaunchedEffect(items, selectedIndex) {
        if (items.isEmpty() || selectedIndex !in items.indices) {
            canApplyCenteredSelection = true
            return@LaunchedEffect
        }
        canApplyCenteredSelection = false
        val atCenter = centeredItemIndexInLazyList(listState, halfVisibleCount, items.size)
        if (atCenter != selectedIndex) {
            // 프로그램 스크롤 뒤 가짜 "중심==선택" 콜백이 올라가면(한 칸씩 밀림) — 상위에서 [userInteracted] 끄고 맞춤
            userInteracted.value = false
            val targetIndex = (selectedIndex + halfVisibleCount).coerceIn(0, (paddedItems.size - 1).coerceAtLeast(0))
            listState.scrollToItem(targetIndex)
            delay(32)
        }
        canApplyCenteredSelection = true
    }

    val centeredIndex by remember(items.size) {
        derivedStateOf { centeredItemIndexInLazyList(listState, halfVisibleCount, items.size) }
    }

    // 한 칸(항목) 넘어갈 때마다 휠 스냅 + 시계 탁 소리·햅틱(첫 고정은 생략)
    var hapticRef by remember(items) { mutableIntStateOf(-1) }
    LaunchedEffect(centeredIndex, canApplyCenteredSelection) {
        if (!canApplyCenteredSelection) return@LaunchedEffect
        if (items.isEmpty()) {
            hapticRef = -1
            return@LaunchedEffect
        }
        if (centeredIndex in items.indices) {
            if (hapticRef >= 0 && hapticRef != centeredIndex) {
                if (view.isHapticFeedbackEnabled) {
                    @Suppress("DEPRECATION")
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }
            hapticRef = centeredIndex
        }
    }

    LaunchedEffect(centeredIndex, canApplyCenteredSelection) {
        if (!canApplyCenteredSelection) return@LaunchedEffect
        if (!userInteracted.value) return@LaunchedEffect
        if (centeredIndex in items.indices && centeredIndex != latestSelectedIndex.value) {
            latestOnSelectedIndexChange.value(centeredIndex)
        }
    }

    val fling = rememberSnapFlingBehavior(lazyListState = listState)

    Box(
        modifier = modifier.height(itemHeight * visibleCount)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = fling,
            overscrollEffect = null,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(false)
                        userInteracted.value = true
                    }
                },
        ) {
            itemsIndexed(paddedItems) { index, item ->
                val isSelected = (index - halfVisibleCount) == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    if (item != null) {
                        val textAlpha = if (isSelected) selectedOpacity else unselectedOpacity
                        Text(
                            text = label(item),
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(alpha = textAlpha),
                            style = textStyle,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
