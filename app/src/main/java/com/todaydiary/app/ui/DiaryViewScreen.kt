package com.todaydiary.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.DiaryBodyField
import com.todaydiary.app.ui.theme.DiaryBodyText
import com.todaydiary.app.ui.theme.DiaryPlaceholder
import com.todaydiary.app.ui.theme.DiarySurface
import com.todaydiary.app.ui.theme.rememberDiaryTextStyles
import java.time.LocalDate
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun DiaryViewScreen(
    date: LocalDate = LocalDate.now(),
    body: String,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
) {
    val textStyles = rememberDiaryTextStyles()
    val headerDateText = remember(date) { formatHeaderDate(date) }
    val interactionSource = remember { MutableInteractionSource() }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = DiarySurface,
        unfocusedContainerColor = DiarySurface,
        disabledContainerColor = DiarySurface,
        focusedIndicatorColor = MaterialTheme.colorScheme.background,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
        disabledIndicatorColor = MaterialTheme.colorScheme.background,
        cursorColor = MaterialTheme.colorScheme.onSurface,
    )

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
            Spacer(modifier = Modifier.height(54.dp))
            DiaryTopBar(
                centerText = headerDateText,
                centerTextStyle = textStyles.headerDate,
                onBack = onBack,
            )

            Spacer(modifier = Modifier.height(46.dp))

            Text(
                text = "오늘의 일기",
                color = MaterialTheme.colorScheme.onSurface,
                style = textStyles.title,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { detectTapGestures(onDoubleTap = { onEdit() }) }
            ) {
                DiaryBodyField(
                    value = body,
                    onValueChange = null,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = textStyles.bodyInput.copy(color = DiaryBodyText),
                    placeholderText = "",
                    placeholderStyle = textStyles.placeholder,
                    placeholderColor = DiaryPlaceholder,
                    colors = colors,
                    keyboardOptions = KeyboardOptions.Default,
                    interactionSource = interactionSource,
                )
            }
        }
    }
}

