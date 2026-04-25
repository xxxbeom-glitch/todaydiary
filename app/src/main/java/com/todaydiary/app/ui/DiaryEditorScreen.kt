package com.todaydiary.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.todaydiary.app.ui.theme.DiaryBodyText
import com.todaydiary.app.ui.theme.DiaryAccentRed
import com.todaydiary.app.ui.theme.DiaryPlaceholder
import com.todaydiary.app.ui.theme.DiarySurface
import com.todaydiary.app.ui.theme.rememberDiaryTextStyles
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.DiaryBodyField
import com.todaydiary.app.R
import java.time.LocalDate

@Composable
fun DiaryEditorScreen(
    date: LocalDate = LocalDate.now(),
    body: String = "",
    onBodyChange: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    var content by rememberSaveable { mutableStateOf(body) }
    val inputInteractionSource = remember { MutableInteractionSource() }
    val headerDateText = remember(date) { formatHeaderDate(date) }
    val isDoneEnabled by remember { derivedStateOf { content.isNotBlank() } }
    val textStyles = rememberDiaryTextStyles()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            ) {
                // Matches Figma Frame 5 (x=16, y=54, h=28)
                Spacer(modifier = Modifier.height(54.dp))
                DiaryTopBar(
                    centerText = headerDateText,
                    centerTextStyle = textStyles.headerDate,
                    onBack = onBack,
                    right = {
                        TextButton(
                            onClick = onDone,
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Text(
                                text = "완료",
                                color = if (isDoneEnabled) DiaryAccentRed else DiaryPlaceholder,
                                style = textStyles.headerDone,
                            )
                        }
                    }
                )

                // Gap between top row and title block: 46px in Figma (y: 54+28 -> 82, title starts at 128)
                Spacer(modifier = Modifier.height(46.dp))

                // Matches Figma Frame 4 (itemSpacing=6)
                Text(
                    text = "오늘의 일기",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = textStyles.title,
                )
                Spacer(modifier = Modifier.height(16.dp))

                val textStyle = textStyles.bodyInput.copy(color = DiaryBodyText)
                val colors = TextFieldDefaults.colors(
                    focusedContainerColor = DiarySurface,
                    unfocusedContainerColor = DiarySurface,
                    disabledContainerColor = DiarySurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    disabledIndicatorColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                )

                DiaryBodyField(
                    value = content,
                    onValueChange = { v ->
                        content = v
                        onBodyChange(v)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = textStyle,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default,
                    ),
                    interactionSource = inputInteractionSource,
                    placeholderText = "어떤 하루를 보냈는지 적어볼까요",
                    placeholderStyle = textStyles.placeholder,
                    placeholderColor = DiaryPlaceholder,
                    colors = colors,
                )
            }
        }
    )
}

