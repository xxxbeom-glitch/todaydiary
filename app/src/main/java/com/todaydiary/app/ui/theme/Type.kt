package com.todaydiary.app.ui.theme

import android.graphics.Typeface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class DiaryFonts(
    val light: FontFamily,
    val bold: FontFamily,
)

@Composable
fun rememberDiaryFonts(): DiaryFonts {
    val context = LocalContext.current

    val bookkMyungjoLight = remember {
        Typeface.createFromAsset(context.assets, "BookkMyungjo_Light.ttf")
    }
    val bookkMyungjoBold = remember {
        Typeface.createFromAsset(context.assets, "BookkMyungjo_Bold.ttf")
    }

    val lightFamily = remember(bookkMyungjoLight) { FontFamily(bookkMyungjoLight) }
    val boldFamily = remember(bookkMyungjoBold) { FontFamily(bookkMyungjoBold) }

    return remember(lightFamily, boldFamily) { DiaryFonts(light = lightFamily, bold = boldFamily) }
}

@Composable
fun TodayDiaryTypography(): Typography {
    val fonts = rememberDiaryFonts()

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = fonts.light,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fonts.bold,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = fonts.bold,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
    )
}

data class DiaryTextStyles(
    val headerDate: TextStyle,
    val headerDone: TextStyle,
    val title: TextStyle,
    val placeholder: TextStyle,
    val bodyInput: TextStyle,
    val listHeaderTitle: TextStyle,
    val listMonthTitle: TextStyle,
    val listItemDate: TextStyle,
    val listItemDateSelected: TextStyle,
)

@Composable
fun rememberDiaryTextStyles(): DiaryTextStyles {
    val fonts = rememberDiaryFonts()

    return remember(fonts) {
        DiaryTextStyles(
            headerDate = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.225).sp, // -1.5%
            ),
            headerDone = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                letterSpacing = 0.13.sp, // 1%
            ),
            title = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                letterSpacing = (-0.36).sp, // -1.5% of 24px base
            ),
            placeholder = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 34.sp,
                letterSpacing = 0.16.sp, // 1%
            ),
            bodyInput = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 34.sp,
            ),
            listHeaderTitle = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                letterSpacing = (-0.225).sp,
            ),
            listMonthTitle = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                letterSpacing = (-0.36).sp,
            ),
            listItemDate = TextStyle(
                fontFamily = fonts.light,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                lineHeight = 33.sp,
                letterSpacing = 0.252.sp,
            ),
            listItemDateSelected = TextStyle(
                fontFamily = fonts.bold,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 33.sp,
                letterSpacing = 0.252.sp,
            ),
        )
    }
}