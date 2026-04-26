package com.todaydiary.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val figmaDesignWidth = 360.dp

/**
 * Figma 디자인 너비(360dp)를 기준으로 현재 기기 화면 너비에 비례하여
 * 동적인 sp(Scale-independent Pixel) 값을 반환합니다.
 * @receiver Int Figma에서의 폰트 사이즈 (px 단위)
 * @return 현재 기기에 맞게 스케일링된 TextUnit(sp)
 */
@Composable
fun Int.responsiveSp(): TextUnit {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val scaleFactor = screenWidth.value / figmaDesignWidth.value
    return (this * scaleFactor).sp
}
