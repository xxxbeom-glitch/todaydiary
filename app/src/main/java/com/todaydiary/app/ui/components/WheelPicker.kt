package com.todaydiary.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> WheelPicker(
    items: List<T>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Int = 32,
    itemSpacing: Int = 16,
    visibleCount: Int = 3,
    textStyle: TextStyle,
    label: (T) -> String,
) {
    val itemHeightDp = itemHeight.dp
    val itemSpacingDp = itemSpacing.dp
    val slotHeightDp = itemHeightDp + itemSpacingDp
    val listState = rememberLazyListState()
    val latestOnSelectedIndexChange = rememberUpdatedState(onSelectedIndexChange)
    val density = LocalDensity.current

    val centerOffset = (visibleCount / 2)
    val paddedItems = remember(items) {
        // Add padding items to allow first/last to center
        listOf<T?>(null) + items.map { it as T? } + listOf<T?>(null)
    }

    LaunchedEffect(items, selectedIndex) {
        // +1 because of leading null padding
        val target = (selectedIndex + 1 - centerOffset).coerceAtLeast(0)
        listState.scrollToItem(target)
    }

    val centeredIndex by remember {
        derivedStateOf {
            val first = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val halfSlotPx = with(density) { slotHeightDp.toPx() / 2f }
            val raw = first + if (offset > halfSlotPx) 1 else 0
            // convert back to original items index (minus leading pad)
            (raw + centerOffset - 1).coerceIn(0, items.lastIndex)
        }
    }

    LaunchedEffect(centeredIndex) {
        latestOnSelectedIndexChange.value(centeredIndex)
    }

    Box(
        modifier = modifier.height(
            (itemHeightDp * visibleCount) + (itemSpacingDp * (visibleCount - 1))
        )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(paddedItems) { index, item ->
                val isCenter = (index - centerOffset - 1) == centeredIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(slotHeightDp),
                ) {
                    Text(
                        text = item?.let(label) ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp),
                        style = textStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isCenter) 1f else 0.25f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

