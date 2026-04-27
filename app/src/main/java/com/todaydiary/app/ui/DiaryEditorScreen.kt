package com.todaydiary.app.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.todaydiary.app.BuildConfig
import com.todaydiary.app.R
import com.todaydiary.app.ui.components.DiaryTopBar
import com.todaydiary.app.ui.components.HeaderPngIconButton
import com.todaydiary.app.ui.components.ThemedPngIcon
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditorScreen(
    onBack: () -> Unit = {},
    initialDate: LocalDate = LocalDate.now(),
    initialBody: String = "",
    onAutoSave: (date: LocalDate, body: String, writtenAt: Instant) -> Unit = { _, _, _ -> },
) {
    // initialBody를 remember 키로 쓰면(자동저장으로 부모의 draft 문자열이 갱신될 때마다) 전부 재초기화되며
    // 커서/IME 조합이 튑니다. 편집 세션 id는 MainActivity의 key(editorResetKey) + 화면 전환으로 맞춥니다.
    var diaryValue by remember { mutableStateOf(TextFieldValue(initialBody)) }
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
    val bodyColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
    val placeholderTextStyle = bodyTextStyle.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))

    val density = LocalDensity.current
    val lineHeightPx = with(density) { bodyTextStyle.lineHeight.toPx() }
    val firstLineY = with(density) { bodyTextStyle.fontSize.toPx() + 10.dp.toPx() }
    val lineColor = MaterialTheme.colorScheme.outline
    val cursorColor = MaterialTheme.colorScheme.primary
    val cursorVisualHeightPx = with(density) { bodyTextStyle.fontSize.toPx() * 1.08f }
    val cursorStrokePx = with(density) { 1.2.dp.toPx() }
    /** 삽입 줄 박스 중앙 기준; 순수 중앙은 살짝 위로 보일 때가 있어 아주 소량만 아래로 */
    val cursorVerticalNudgePx = with(density) { 0.65.dp.toPx() }

    // 시스템 뒤로가기도 상단 버튼과 같이 마지막 본문을 반영(빈 문자열이면 저장/클라 초기화)
    BackHandler {
        onAutoSave(today, diaryText, Instant.now())
        onBack()
    }

    Scaffold(
        topBar = {
            DiaryTopBar(
                left = {
                    HeaderPngIconButton(
                        onClick = {
                            onAutoSave(today, diaryText, Instant.now())
                            onBack()
                        },
                        resId = R.drawable.ico_back,
                        contentDescription = "Back",
                    )
                },
                center = {
                    Text(
                        text = today.format(formatter),
                        style = headerTextStyle,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                right = {
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemedPngIcon(
                            resId = R.drawable.ico_more,
                            contentDescription = "More",
                            modifier = Modifier
                                .size(28.dp)
                                .alpha(0.2f),
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val cursorAlpha by rememberInfiniteTransition(label = "").animateFloat(
            initialValue = 1f, targetValue = 0f, animationSpec = infiniteRepeatable(animation = tween(500)), label = ""
        )
        var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

        // 디버그 빌드만: Logcat 필터 `DiaryCursor` — 커서 보정 값 맞출 때 붙여 보내기 좋음
        LaunchedEffect(isFocused, diaryText, diaryValue.selection, textLayoutResult, cursorVerticalNudgePx) {
            if (!BuildConfig.DEBUG || !isFocused) return@LaunchedEffect
            val layout = textLayoutResult ?: return@LaunchedEffect
            val len = layout.layoutInput.text.text.length
            val off = diaryValue.selection.end.coerceIn(0, len)
            val r = layout.getCursorRect(off)
            val line = layout.getLineForOffset(off)
            val baseline = layout.getLineBaseline(line)
            val drawTop = caretDrawTopPx(r.top, r.bottom, baseline, cursorVisualHeightPx, cursorVerticalNudgePx)
            val fontPx = with(density) { bodyTextStyle.fontSize.toPx() }
            Log.d(
                "DiaryCursor",
                "off=$off line=$line rect=[${r.top},${r.bottom}] baseline=$baseline " +
                    "drawTop=$drawTop drawBottom=${drawTop + cursorVisualHeightPx} " +
                    "nudgePx=$cursorVerticalNudgePx visH=$cursorVisualHeightPx fontPx=$fontPx lineHpx=$lineHeightPx",
            )
        }

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
                        onAutoSave(today, diaryValue.text, Instant.now())
                    }
                },
                onTextLayout = { textLayoutResult = it },
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Column 내에서 남은 공간을 모두 차지
                    .verticalScroll(scrollState)
                    .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                textStyle = bodyTextStyle.copy(color = bodyColor),
                // lineHeight가 크면 기본 커서도 줄 전체로 커짐 → 숨기고 아래에서 글자 높이만큼만 그림
                cursorBrush = SolidColor(Color.Transparent),
                decorationBox = { innerTextField ->
                    Box {
                        // 배경 줄무늬
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val lineCount = (size.height / lineHeightPx).toInt() + 10
                            for (i in 0..lineCount) {
                                val y = firstLineY + (i * lineHeightPx)
                                drawLine(lineColor, start = Offset(x = 0f, y = y), end = Offset(x = size.width, y = y), strokeWidth = 1f)
                            }
                        }

                        if (diaryText.isEmpty() && !isFocused) {
                            Text(text = "오늘의 하루를 기록하세요", style = placeholderTextStyle)
                        }

                        innerTextField()

                        // 텍스트 위: 줄 박스 중앙(midY)은 lineHeight 크면 베이스라인과 어긋남 → 베이스라인 기준 + rect 안 클램프
                        if (isFocused) {
                            textLayoutResult?.let { layout ->
                                val len = layout.layoutInput.text.text.length
                                val off = diaryValue.selection.end.coerceIn(0, len)
                                val r = layout.getCursorRect(off)
                                val line = layout.getLineForOffset(off)
                                val baseline = layout.getLineBaseline(line)
                                val top = caretDrawTopPx(r.top, r.bottom, baseline, cursorVisualHeightPx, cursorVerticalNudgePx)
                                Canvas(modifier = Modifier.matchParentSize()) {
                                    drawLine(
                                        color = cursorColor.copy(alpha = cursorAlpha),
                                        start = Offset(r.left, top),
                                        end = Offset(r.left, top + cursorVisualHeightPx),
                                        strokeWidth = cursorStrokePx
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

/** [getCursorRect] 세로 범위 안에서만, 막대가 베이스라인 근처를 가리키도록 배치(큰 lineHeight 2번째 줄 등) */
private fun caretDrawTopPx(
    rectTop: Float,
    rectBottom: Float,
    baselineY: Float,
    caretHeightPx: Float,
    nudgePx: Float,
): Float {
    val idealTop = baselineY - caretHeightPx * 0.52f + nudgePx
    val maxTop = (rectBottom - caretHeightPx).coerceAtLeast(rectTop)
    return idealTop.coerceIn(rectTop, maxTop)
}
