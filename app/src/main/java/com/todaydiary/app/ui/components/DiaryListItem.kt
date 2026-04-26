package com.todaydiary.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.todaydiary.app.R

@Composable
fun DiaryListItem(
    title: String,
    preview: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    previewStyle: TextStyle = MaterialTheme.typography.bodySmall,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    previewColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary,
    // spacing/padding
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 8.dp,
    textBlockVerticalPadding: Dp = 8.dp,
    gapBetweenTextLines: Dp = 4.dp,
    gapBetweenTextAndMore: Dp = 10.dp,
    previewWidth: Dp = 220.dp,
    onClick: (() -> Unit)? = null,
    // trailing more
    showMore: Boolean = true,
    moreIconResId: Int = R.drawable.ico_more,
    moreContentDescription: String = "More",
    onClickMore: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.noIndicationClickable(onClick) else Modifier
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gapBetweenTextAndMore),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = textBlockVerticalPadding),
            verticalArrangement = Arrangement.spacedBy(gapBetweenTextLines),
        ) {
            Text(
                text = title,
                style = titleStyle,
                color = titleColor,
            )
            Text(
                text = preview,
                style = previewStyle,
                color = previewColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(previewWidth),
            )
        }

        if (showMore) {
            IconButton(
                onClick = onClickMore,
                modifier = Modifier.size(28.dp),
            ) {
                Image(
                    painter = painterResource(id = moreIconResId),
                    contentDescription = moreContentDescription,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

