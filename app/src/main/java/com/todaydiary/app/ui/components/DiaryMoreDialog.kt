package com.todaydiary.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.todaydiary.app.ui.responsiveSp

@Composable
fun DiaryMoreDialog(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val cornerR = popupDp(12f)
    val panelW = popupDp(204f)
    val panelH = popupDp(120f)
    val pad = popupDp(18f)
    val rowGap = popupDp(12f)
    val rowW = popupDp(168f)
    val rowH = popupDp(36f)
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
                .background(Color(0x99000000)) // #000000 60%
                .noIndicationClickable(onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(cornerR),
                modifier = Modifier
                    .width(panelW)
                    .height(panelH)
            ) {
                val itemStyle = MaterialTheme.typography.labelLarge.copy(
                    fontSize = labelSp.responsiveSp(),
                    fontWeight = FontWeight.Normal,
                )

                Column(
                    modifier = Modifier.padding(pad),
                    verticalArrangement = Arrangement.spacedBy(rowGap),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(rowW)
                            .height(rowH)
                            .noIndicationClickable {
                                onDismiss()
                                onEdit()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "수정",
                            style = itemStyle,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(rowW)
                            .height(rowH)
                            .noIndicationClickable {
                                onDismiss()
                                onDelete()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "삭제",
                            style = itemStyle,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

