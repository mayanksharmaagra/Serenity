package com.jrprofessor.serenity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.jrprofessor.serenity.ui.navigation.SerenityMainApp
import com.jrprofessor.serenity.ui.screens.insights.InsightsViewModel
import com.jrprofessor.serenity.ui.screens.mood.MoodViewModel
import com.jrprofessor.serenity.ui.theme.SerenityTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val moodViewModel by viewModels<MoodViewModel>()
    private val insightsViewModel by viewModels<InsightsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SerenityTheme {
                SerenityMainApp(
                    moodViewModel = moodViewModel,
                    insightsViewModel = insightsViewModel
                )
            }
        }
    }
}