package com.todaydiary.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.todaydiary.app.R
import com.todaydiary.app.ui.theme.LocalIsAppDarkMode

@DrawableRes
private fun resolveHeaderPngId(@DrawableRes resId: Int, isAppDark: Boolean): Int {
    if (!isAppDark) return resId
    return when (resId) {
        R.drawable.ico_back -> R.drawable.ico_back_dark
        R.drawable.ico_more -> R.drawable.ico_more_dark
        R.drawable.ico_setting -> R.drawable.ico_setting_dark
        R.drawable.ico_dropdown -> R.drawable.ico_dropdown_dark
        R.drawable.ico_new -> R.drawable.ico_new_dark
        else -> resId
    }
}

/**
 * PNG는 [ColorFilter.tint]로 onBackground에 맞출 수 있음.
 * 앱 **다크 모드**에선 [icons]에서 가져온 흰 선용 에셋(`ico_*_dark`, 원본 `*_white`/`*_whte`)을 쓰고 **틴트 없음**.
 */
@Composable
fun ThemedPngIcon(
    @DrawableRes resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    /** null이면 [androidx.compose.material3.MaterialTheme.colorScheme]의 onBackground */
    tint: Color? = null,
) {
    val isAppDark = LocalIsAppDarkMode.current
    val effectiveId = remember(resId, isAppDark) { resolveHeaderPngId(resId, isAppDark) }
    val usedDarkAsset = isAppDark && effectiveId != resId
    val c = tint ?: MaterialTheme.colorScheme.onBackground
    val colorFilter = if (usedDarkAsset) null else ColorFilter.tint(c)
    Image(
        painter = painterResource(id = effectiveId),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = colorFilter,
    )
}
