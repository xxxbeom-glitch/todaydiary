package com.todaydiary.app.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun DiaryBodyField(
    value: String,
    onValueChange: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    placeholderText: String,
    placeholderStyle: TextStyle,
    placeholderColor: androidx.compose.ui.graphics.Color,
    colors: TextFieldColors,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.onSurface),
) {
    val isEditable = onValueChange != null
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scrollState = rememberScrollState()

    BasicTextField(
        value = value,
        onValueChange = { onValueChange?.invoke(it) },
        modifier = modifier.verticalScroll(scrollState),
        enabled = true,
        readOnly = !isEditable,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        cursorBrush = if (isEditable) cursorBrush else SolidColor(androidx.compose.ui.graphics.Color.Transparent),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = false,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    // Edit: show only when unfocused & empty (matches current behavior)
                    if (isEditable && !isFocused && value.isBlank()) {
                        Text(
                            text = placeholderText,
                            color = placeholderColor,
                            style = placeholderStyle,
                        )
                    }
                },
                contentPadding = PaddingValues(),
                colors = colors,
            )
        }
    )
}

