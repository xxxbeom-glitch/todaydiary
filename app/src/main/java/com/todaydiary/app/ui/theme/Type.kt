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

@Composable
fun TodayDiaryTypography(): Typography {
    val context = LocalContext.current

    val bookkMyungjoLight = remember {
        Typeface.createFromAsset(context.assets, "BookkMyungjo_Light.ttf")
    }
    val bookkMyungjoBold = remember {
        Typeface.createFromAsset(context.assets, "BookkMyungjo_Bold.ttf")
    }

    val bookkMyungjoLightFamily = remember(bookkMyungjoLight) { FontFamily(bookkMyungjoLight) }
    val bookkMyungjoBoldFamily = remember(bookkMyungjoBold) { FontFamily(bookkMyungjoBold) }

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = bookkMyungjoLightFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = bookkMyungjoBoldFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        labelLarge = TextStyle(
            fontFamily = bookkMyungjoBoldFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
    )
}