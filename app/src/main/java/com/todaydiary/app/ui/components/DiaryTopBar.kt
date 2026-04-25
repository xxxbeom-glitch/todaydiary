package com.todaydiary.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R

@Composable
fun DiaryTopBar(
    centerText: String,
    centerTextStyle: TextStyle,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showBack: Boolean = true,
    right: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp),
    ) {
        if (showBack) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(28.dp)
                    .height(28.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ico_back),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Text(
            text = centerText,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 5.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = centerTextStyle,
        )

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            right()
        }
    }
}

