package com.todaydiary.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment

@Composable
fun DiaryTopBar(
    modifier: Modifier = Modifier,
    left: @Composable () -> Unit = {},
    center: @Composable () -> Unit = {},
    right: @Composable () -> Unit = {},
) {
    // Figma (list): top padding 36dp, height 48dp, horizontal padding 16dp
    Surface(color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
                .height(48.dp)
                .padding(horizontal = 16.dp),
        ) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) { left() }
            Box(modifier = Modifier.align(Alignment.Center)) { center() }
            Box(modifier = Modifier.align(Alignment.CenterEnd)) { right() }
        }
    }
}
