package com.todaydiary.app.ui.components

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** 드럼롤·옵션·폰트·더보기 등 오버레이 팝업 공통: 레이아웃·폰트 베이스 약 15% 확대 */
const val POPUP_UI_SCALE = 1.15f

fun popupDp(base: Float): Dp = (base * POPUP_UI_SCALE).dp

/** `15.responsiveSp()` 등: [popupLabelSp] 후 [responsiveSp] */
fun popupLabelSp(base: Int): Int = maxOf(1, (base * POPUP_UI_SCALE).roundToInt())
