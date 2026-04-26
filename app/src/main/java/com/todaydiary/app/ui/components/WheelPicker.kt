package com.todaydiary.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    val listState = rememberLazyListState()
    val latestOnSelectedIndexChange = rememberUpdatedState(onSelectedIndexChange)

    val halfVisibleCount = visibleCount / 2
    val paddedItems = remember(items, halfVisibleCount) {
        val padding = List(halfVisibleCount) { null }
        padding + items + padding
    }

    LaunchedEffect(items, selectedIndex) {
        val targetIndex = (selectedIndex + halfVisibleCount).coerceAtLeast(0)
        listState.scrollToItem(targetIndex)
    }

    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                -1
            } else {
                val viewportCenter = layoutInfo.viewportEndOffset / 2
                val centerItem = visibleItemsInfo.minByOrNull {
                    kotlin.math.abs((it.offset + it.size / 2) - viewportCenter)
                }
                centerItem?.index?.minus(halfVisibleCount) ?: -1
            }
        }
    }

    LaunchedEffect(centeredIndex) {
        if (centeredIndex != -1) {
            latestOnSelectedIndexChange.value(centeredIndex)
        }
    }

    Box(
        modifier = modifier.height(itemHeight * visibleCount)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(paddedItems) { index, item ->
                val isSelected = (index - halfVisibleCount) == selectedIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
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
