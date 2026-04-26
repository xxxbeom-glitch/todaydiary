package com.todaydiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.todaydiary.app.ui.responsiveSp
import com.todaydiary.app.ui.theme.diaryFontFamily

/**
 * 폰트 설정용: 행마다 [diaryFontFamily]로 미리보기, 선택 시 [onSelect]만 호출(부모에서 `fontIndex`로 전앱 갱신).
 */
@Composable
fun FontSelectDialog(
    options: List<String>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val cornerR = popupDp(12f)
    val pad = popupDp(18f)
    val rowGap = popupDp(8f)
    val listMaxH = popupDp(400f)
    val rowMinH = popupDp(40f)
    val minW = popupDp(220f)
    val maxW = popupDp(300f)
    val labelSp = popupLabelSp(15)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .noIndicationClickable(onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(cornerR),
                modifier = Modifier.widthIn(min = minW, max = maxW),
            ) {
                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .heightIn(max = listMaxH)
                        .verticalScroll(scroll)
                        .padding(pad),
                    verticalArrangement = Arrangement.spacedBy(rowGap),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    options.forEachIndexed { index, label ->
                        val alpha = if (index == selectedIndex) 1f else 0.35f
                        val rowFont = diaryFontFamily(index)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = rowMinH)
                                .noIndicationClickable {
                                    onSelect(index)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = labelSp.responsiveSp(),
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = rowFont,
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                            )
                        }
                    }
                }
            }
        }
    }
}
