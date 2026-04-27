package com.todaydiary.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.todaydiary.app.R

/** 0: SUIT (기본), 1: 바른바탕, 2: 카페24 고운밤, 3: 교보 이유빈, 4: 교보 박도연 */
const val DIARY_FONT_COUNT = 5

/** 일기 폰트에 없는 글자는 시스템 산세리프(보통 기기 기본 본문 폰트)로 채움 */
private val systemSansFallback = arrayOf(
    Font(DeviceFontFamilyName("sans-serif"), FontWeight.W400),
    Font(DeviceFontFamilyName("sans-serif"), FontWeight.W500),
    Font(DeviceFontFamilyName("sans-serif"), FontWeight.W600),
    Font(DeviceFontFamilyName("sans-serif"), FontWeight.W700),
)

val suit = FontFamily(
    Font(R.font.suit_regular, FontWeight.W400),
    Font(R.font.suit_medium, FontWeight.W500),
    Font(R.font.suit_semibold, FontWeight.W600),
    Font(R.font.suit_bold, FontWeight.W700),
    *systemSansFallback,
)

fun diaryFontFamily(index: Int): FontFamily = when (index.coerceIn(0, DIARY_FONT_COUNT - 1)) {
    0 -> suit
    1 -> FontFamily(Font(R.font.bareunbatang, FontWeight.W400), *systemSansFallback)
    2 -> FontFamily(Font(R.font.cafe24gounbam, FontWeight.W400), *systemSansFallback)
    3 -> FontFamily(Font(R.font.kyoboleeyoobin, FontWeight.W400), *systemSansFallback)
    4 -> FontFamily(Font(R.font.kyoboparkdoyeon, FontWeight.W400), *systemSansFallback)
    else -> suit
}

fun diaryTypography(family: FontFamily): Typography = Typography(
    bodyLarge = TextStyle( // Body1
        fontFamily = family,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 36.sp
    ),
    bodyMedium = TextStyle( // Body2
        fontFamily = family,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle( // Secondary
        fontFamily = family,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle( // Button
        fontFamily = family,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 36.sp
    )
)
