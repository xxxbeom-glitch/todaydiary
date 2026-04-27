package com.todaydiary.app.ui.theme

import androidx.compose.material3.Typography
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

/**
 * 일기 앱은 body/label 위주로 쓰지만, Material 컴포넌트·다이얼로그 등이 title/headline 슬롯을 쓰면
 * 기본(Roboto 등)으로 남을 수 있어 **모든** Typography 슬롯에 동일 fontFamily를 둔다.
 */
fun diaryTypography(family: FontFamily): Typography {
    val d = Typography()
    // AlertDialog·제목 슬롯 등은 기본 굵기(W500~W600)가 남으면 단일 굵기 폰트가 시스템 글꼴로 떨어짐
    val w = FontWeight.W400
    return Typography(
        displayLarge = d.displayLarge.copy(fontFamily = family, fontWeight = w),
        displayMedium = d.displayMedium.copy(fontFamily = family, fontWeight = w),
        displaySmall = d.displaySmall.copy(fontFamily = family, fontWeight = w),
        headlineLarge = d.headlineLarge.copy(fontFamily = family, fontWeight = w),
        headlineMedium = d.headlineMedium.copy(fontFamily = family, fontWeight = w),
        headlineSmall = d.headlineSmall.copy(fontFamily = family, fontWeight = w),
        titleLarge = d.titleLarge.copy(fontFamily = family, fontWeight = w),
        titleMedium = d.titleMedium.copy(fontFamily = family, fontWeight = w),
        titleSmall = d.titleSmall.copy(fontFamily = family, fontWeight = w),
        bodyLarge = d.bodyLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 36.sp,
        ),
        bodyMedium = d.bodyMedium.copy(
            fontFamily = family,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = d.bodySmall.copy(
            fontFamily = family,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelLarge = d.labelLarge.copy(
            fontFamily = family,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 36.sp,
        ),
        labelMedium = d.labelMedium.copy(fontFamily = family, fontWeight = w),
        labelSmall = d.labelSmall.copy(fontFamily = family, fontWeight = w),
    )
}
