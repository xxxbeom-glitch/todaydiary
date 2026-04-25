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
import androidx.compose.ui.platform.LocalContext

private val DiaryLightColorScheme = lightColorScheme(
    primary = DiaryOnSurface,
    onPrimary = DiarySurface,
    background = DiaryBackground,
    onBackground = DiaryOnSurface,
    surface = DiarySurface,
    onSurface = DiaryOnSurface,
    surfaceVariant = DiarySurface,
    onSurfaceVariant = DiaryOnSurface,
    outline = DiaryDivider,
)

private val DiaryDarkColorScheme = darkColorScheme(
    primary = DiaryBackground,
    onPrimary = DiaryOnSurface,
    background = DiaryBackground,
    onBackground = DiaryOnSurface,
    surface = DiarySurface,
    onSurface = DiaryOnSurface,
    surfaceVariant = DiarySurface,
    onSurfaceVariant = DiaryOnSurface,
    outline = DiaryDivider,
)

@Composable
fun TodayDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DiaryDarkColorScheme
        else -> DiaryLightColorScheme
    }

    CompositionLocalProvider(LocalIndication provides NoIndication) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TodayDiaryTypography(),
            content = content
        )
    }
}