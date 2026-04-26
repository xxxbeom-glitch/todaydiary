package com.todaydiary.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 토바 헤더용: 리플 대신 **눌렀을 때** 아이콘에만 80% 투명도(기본 100% → 누름 80%)를 준다.
 */
@Composable
fun HeaderPngIconButton(
    onClick: () -> Unit,
    @DrawableRes resId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier.size(28.dp),
    tint: Color? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val contentAlpha = if (pressed) 0.8f else 1f
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        ThemedPngIcon(
            resId = resId,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(28.dp)
                .alpha(contentAlpha),
            tint = tint,
        )
    }
}
