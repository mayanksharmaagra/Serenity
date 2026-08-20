package com.jrprofessor.serenity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jrprofessor.serenity.data.local.SerenityDatabase
import com.jrprofessor.serenity.data.repository.MoodRepository
import com.jrprofessor.serenity.ui.navigation.SerenityMainApp
import com.jrprofessor.serenity.ui.screens.insights.InsightsViewModel
import com.jrprofessor.serenity.ui.screens.mood.MoodViewModel
import com.jrprofessor.serenity.ui.theme.SerenityTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { SerenityDatabase.getDatabase(this) }
    private val repository by lazy { MoodRepository(database.moodDao()) }

    private val moodViewModel by viewModels<MoodViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MoodViewModel(repository) as T
                }
            }
        }
    )

    private val insightsViewModel by viewModels<InsightsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return InsightsViewModel(repository) as T
                }
            }
        }
    )

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