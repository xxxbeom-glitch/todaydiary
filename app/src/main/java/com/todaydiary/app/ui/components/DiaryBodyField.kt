package com.todaydiary.app.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryBodyField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = false,
                visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        text = "오늘의 일기를 작성해주세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                )
            )
        }
    )
}
