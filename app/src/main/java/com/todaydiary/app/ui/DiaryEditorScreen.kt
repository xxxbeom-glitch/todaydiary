package com.todaydiary.app.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R
import com.todaydiary.app.ui.components.DiaryTopBar
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditorScreen(
    onBack: () -> Unit = {},
    initialDate: LocalDate = LocalDate.now(),
    initialBody: String = "",
    onAutoSave: (date: LocalDate, body: String) -> Unit = { _, _ -> },
) {
    var diaryValue by remember(initialBody) { mutableStateOf(TextFieldValue(initialBody)) }
    val diaryText = diaryValue.text
    val today = initialDate
    val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var pendingSaveJob: Job? by remember { mutableStateOf(null) }

    val headerTextStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.responsiveSp(), fontWeight = FontWeight.Medium)
    val bodyTextStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.responsiveSp(), lineHeight = 36.responsiveSp())
    val placeholderTextStyle = bodyTextStyle.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))

    val density = LocalDensity.current
    val lineHeightPx = with(density) { bodyTextStyle.lineHeight.toPx() }
    val firstLineY = with(density) { bodyTextStyle.fontSize.toPx() + 10.dp.toPx() }
    val cursorHeight = with(density) { bodyTextStyle.fontSize.toPx() * 1.2f }
    val cursorStrokeWidth = with(density) { 1.5.dp.toPx() }
    val lineColor = MaterialTheme.colorScheme.outline
    val cursorColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            DiaryTopBar(
                left = {
                    IconButton(
                        onClick = {
                            onAutoSave(today, diaryText)
                            onBack()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
                center = {
                    Text(
                        text = today.format(formatter),
                        style = headerTextStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                right = {
                    IconButton(
                        onClick = { onAutoSave(today, diaryText) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ico_more),
                            contentDescription = "Save",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
        val cursorAlpha by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 1f, targetValue = 0f, animationSpec = infiniteRepeatable(animation = tween(500)), label = ""
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            BasicTextField(
                value = diaryValue,
                onValueChange = {
                    diaryValue = it
                    // 텍스트 변경 시 스크롤을 가장 아래로 이동
                    coroutineScope.launch { scrollState.animateScrollTo(scrollState.maxValue) }
                    // 작성 중에는 기기에 우선 자동저장 (debounce)
                    pendingSaveJob?.cancel()
                    pendingSaveJob = coroutineScope.launch {
                        kotlinx.coroutines.delay(400)
                        onAutoSave(today, diaryValue.text)
                    }
                },
                onTextLayout = { textLayoutResult = it },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Column 내에서 남은 공간을 모두 차지
                    .verticalScroll(scrollState)
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                textStyle = bodyTextStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(Color.Transparent),
                decorationBox = { innerTextField ->
                    Box {
                        // 1. 줄무늬 그리기
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val lineCount = (size.height / lineHeightPx).toInt() + 10
                            for (i in 0..lineCount) {
                                val y = firstLineY + (i * lineHeightPx)
                                drawLine(lineColor, start = Offset(x = 0f, y = y), end = Offset(x = size.width, y = y), strokeWidth = 1f)
                            }
                        }

                        // 2. 플레이스홀더 또는 커서 그리기
                        if (diaryText.isEmpty() && !isFocused) {
                            Text(text = "오늘의 하루를 기록하세요", style = placeholderTextStyle)
                        } else if (isFocused) {
                            textLayoutResult?.let { layoutResult ->
                                val layoutTextLength = layoutResult.layoutInput.text.text.length
                                val selectionEnd = diaryValue.selection.end
                                val safeOffset = selectionEnd.coerceIn(0, layoutTextLength)
                                val cursorRect = layoutResult.getCursorRect(safeOffset)
                                Canvas(modifier = Modifier.matchParentSize()) {
                                    drawLine(
                                        color = cursorColor.copy(alpha = cursorAlpha),
                                        start = Offset(cursorRect.left, cursorRect.top),
                                        end = Offset(cursorRect.left, cursorRect.top + cursorHeight),
                                        strokeWidth = cursorStrokeWidth
                                    )
                                }
                            }
                        }

                        // 3. 실제 텍스트 필드 그리기 (가장 위에)
                        innerTextField()
                    }
                }
            )
        }
    }
}
