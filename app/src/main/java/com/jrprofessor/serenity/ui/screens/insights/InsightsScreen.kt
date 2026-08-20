package com.jrprofessor.serenity.ui.screens.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.ui.components.MoodFlowChart
import com.jrprofessor.serenity.ui.components.ReflectionListItem
import com.jrprofessor.serenity.ui.components.SegmentedControl
import com.jrprofessor.serenity.ui.components.StreakBadge
import com.jrprofessor.serenity.ui.screens.journal.ReflectionDetailSheet
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary
import com.jrprofessor.serenity.ui.theme.serenityBackground

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDays by viewModel.selectedDaysFilter.collectAsState()
    val filteredEntries by viewModel.filteredEntries.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val streakDays by viewModel.currentStreak.collectAsState()

    var selectedEntryForDetail by remember { mutableStateOf<MoodEntry?>(null) }

    val daysOptions = listOf(7, 30, 90)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .serenityBackground()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Heading: "Your Journey"
        Text(
            text = "Your Journey",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Subtext: "Track your emotional landscape over time."
        Text(
            text = "Track your emotional landscape over time.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary,
                fontSize = 15.sp
            )
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Segmented Control: 7 Days / 30 Days / 90 Days
        SegmentedControl(
            items = daysOptions,
            selectedItem = selectedDays,
            onItemSelected = { days ->
                viewModel.setDaysFilter(days)
            },
            labelProvider = { days -> "$days Days" }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card 1: Mood Flow Waveform Chart
        MoodFlowChart(
            entries = if (filteredEntries.isNotEmpty()) filteredEntries else allEntries
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Streak Badge (e.g. "🔥 5-day journaling streak")
        StreakBadge(streakDays = streakDays)

        Spacer(modifier = Modifier.height(24.dp))

        // Section: "Recent Reflections"
        Text(
            text = "Recent Reflections",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Reflections List Items
        val displayEntries = if (allEntries.isNotEmpty()) allEntries else filteredEntries
        if (displayEntries.isEmpty()) {
            Text(
                text = "No reflections yet for this period. Start your first check-in from the Mood tab!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                displayEntries.forEach { entry ->
                    ReflectionListItem(
                        entry = entry,
                        onClick = {
                            selectedEntryForDetail = entry
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom bar
    }

    // Modal Bottom Sheet Detail View
    selectedEntryForDetail?.let { entry ->
        ReflectionDetailSheet(
            entry = entry,
            onDismiss = { selectedEntryForDetail = null },
            onDelete = {
                viewModel.deleteEntry(entry)
                selectedEntryForDetail = null
            }
        )
    }
}
