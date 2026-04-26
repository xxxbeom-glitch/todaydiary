package com.todaydiary.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.todaydiary.app.R

val suit = FontFamily(
    Font(R.font.suit_regular, FontWeight.W400),
    Font(R.font.suit_medium, FontWeight.W500),
    Font(R.font.suit_semibold, FontWeight.W600),
    Font(R.font.suit_bold, FontWeight.W700)
)

val AppTypography = Typography(
    bodyLarge = TextStyle( // Body1
        fontFamily = suit,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 36.sp
    ),
    bodyMedium = TextStyle( // Body2
        fontFamily = suit,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle( // Secondary
        fontFamily = suit,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle( // Button
        fontFamily = suit,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 36.sp
    )
)
