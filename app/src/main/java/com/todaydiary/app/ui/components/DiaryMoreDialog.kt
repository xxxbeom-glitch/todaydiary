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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.todaydiary.app.ui.responsiveSp

@Composable
fun DiaryMoreDialog(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
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
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(204.dp)
                    .height(120.dp)
            ) {
                val itemStyle = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 15.responsiveSp(),
                    fontWeight = FontWeight.Medium,
                )

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .width(168.dp)
                            .height(36.dp)
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
                            .width(168.dp)
                            .height(36.dp)
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

