package com.todaydiary.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.todaydiary.app.ui.theme.TodayDiaryTheme
import com.todaydiary.app.ui.DiaryEditorScreen
import com.todaydiary.app.ui.DiaryListScreen
import com.todaydiary.app.ui.DiaryViewScreen
import com.todaydiary.app.ui.SettingsScreen
import com.todaydiary.app.ui.import.PencakeImporter
import com.todaydiary.app.ui.models.DiaryEntry
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayDiaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    var screen by remember { mutableStateOf("editor") }
                    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
                    var selectedBody by remember { mutableStateOf("") }
                    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
                    val entries = remember { mutableStateListOf<DiaryEntry>() }
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        if (entries.isEmpty()) {
                            val imported = PencakeImporter.loadFromAssets(context)
                            entries.addAll(imported)
                            val latest = imported.maxByOrNull { it.date }
                            if (latest != null) {
                                currentMonth = latest.date.withDayOfMonth(1)
                            }
                        }
                    }

                    BackHandler {
                        when (screen) {
                            "view" -> screen = "list"
                            "editor" -> screen = "list"
                            "settings" -> screen = "list"
                            "list" -> finish()
                            else -> finish()
                        }
                    }

                    when (screen) {
                        "list" -> DiaryListScreen(
                            onBack = { /* no-op */ },
                            month = currentMonth,
                            entries = entries
                                .asSequence()
                                .filter { it.date.year == currentMonth.year && it.date.month == currentMonth.month }
                                .sortedByDescending { it.date }
                                .toList(),
                            onSelectItem = { entry ->
                                selectedDate = entry.date
                                selectedBody = entry.body
                                // photos are view-only for now
                                screen = "view"
                            },
                            onPullDownToCompose = {
                                val now = LocalDate.now()
                                selectedDate = now
                                selectedBody = ""
                                currentMonth = now.withDayOfMonth(1)
                                screen = "editor"
                            },
                            onSettings = {
                                screen = "settings"
                            },
                            yearRange = run {
                                val years = entries.map { it.date.year }
                                val minY = years.minOrNull() ?: currentMonth.year
                                val maxY = years.maxOrNull() ?: currentMonth.year
                                minY..maxY
                            },
                            onMonthChange = { newMonth ->
                                currentMonth = newMonth
                            },
                        )
                        "settings" -> SettingsScreen(
                            onBack = { screen = "list" }
                        )
                        "view" -> DiaryViewScreen(
                            date = selectedDate,
                            body = selectedBody,
                            photos = entries.firstOrNull { it.date == selectedDate }?.photos.orEmpty(),
                            onBack = { screen = "list" },
                            onEdit = { screen = "editor" },
                        )
                        else -> DiaryEditorScreen(
                            date = selectedDate,
                            body = selectedBody,
                            onBodyChange = { selectedBody = it },
                            onBack = {
                                currentMonth = selectedDate.withDayOfMonth(1)
                                screen = "list"
                            },
                            onDone = {
                                // Save: upsert by date, then go to current month's list
                                val idx = entries.indexOfFirst { it.date == selectedDate }
                                if (idx >= 0) entries[idx] = DiaryEntry(date = selectedDate, body = selectedBody)
                                else entries.add(DiaryEntry(date = selectedDate, body = selectedBody))
                                currentMonth = selectedDate.withDayOfMonth(1)
                                screen = "list"
                            },
                        )
                    }
                }
            }
        }
    }
}