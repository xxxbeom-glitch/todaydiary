package com.todaydiary.app.ui.theme

import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private val AppLightColorScheme = lightColorScheme(
    background = Background,
    onBackground = TextBody,
    surface = PopupBackground,
    onSurface = TextBody,
    primary = TextBody,
    onPrimary = Background,
    secondary = TextSecondary,
    outline = BorderUnderline
)

private val AppDarkColorScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkTextBody,
    surface = DarkSurface,
    onSurface = DarkTextBody,
    primary = DarkTextBody,
    onPrimary = DarkBackground,
    secondary = DarkTextSecondary,
    outline = DarkBorder
)

/** in-app [TodayDiaryTheme] 다크·라이트 (`darkTheme` 인자). 시스템 `isSystemInDarkTheme`와는 별개. */
val LocalIsAppDarkMode = compositionLocalOf { false }

@Composable
fun TodayDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontIndex: Int = 0,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AppDarkColorScheme
        else -> AppLightColorScheme
    }
    val typography = remember(fontIndex) {
        diaryTypography(diaryFontFamily(fontIndex))
    }

    CompositionLocalProvider(
        LocalIndication provides NoIndication,
        LocalIsAppDarkMode provides darkTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}
