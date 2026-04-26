package com.todaydiary.app.data

import android.content.Context

class ThemePreferenceStore(
    private val context: Context,
) {
    private val prefs by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    /** true: Dark, false: Light (기기 기본 X — 설정의 Light/Dark만 사용) */
    fun isDarkModeEnabled(): Boolean = prefs.getBoolean(KEY_DARK, false)

    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK, enabled).apply()
    }

    /** 0 SUIT, 1~4: addfont ttf. [com.todaydiary.app.ui.theme.DIARY_FONT_COUNT] */
    fun getFontIndex(): Int = prefs.getInt(KEY_FONT, 0).coerceIn(0, 4)

    fun setFontIndex(index: Int) {
        prefs.edit().putInt(KEY_FONT, index.coerceIn(0, 4)).apply()
    }

    companion object {
        private const val PREFS_NAME = "today_diary_settings"
        private const val KEY_DARK = "dark_mode_enabled"
        private const val KEY_FONT = "font_index"
    }
}
