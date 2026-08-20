package com.jrprofessor.serenity.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.ui.components.ReflectionListItem
import com.jrprofessor.serenity.ui.screens.insights.InsightsViewModel
import com.jrprofessor.serenity.ui.theme.AccentLavender
import com.jrprofessor.serenity.ui.theme.GlassBorder
import com.jrprofessor.serenity.ui.theme.TextMuted
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary
import com.jrprofessor.serenity.ui.theme.serenityBackground

@Composable
fun JournalListScreen(
    viewModel: InsightsViewModel,
    modifier: Modifier = Modifier
) {
    val allEntries by viewModel.allEntries.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMoodFilter by remember { mutableStateOf<MoodType?>(null) }
    var selectedEntryForDetail by remember { mutableStateOf<MoodEntry?>(null) }

    val filteredList = allEntries.filter { entry ->
        val matchesQuery = searchQuery.isBlank() ||
                entry.journalText.contains(searchQuery, ignoreCase = true) ||
                entry.themes.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesMood = selectedMoodFilter == null || entry.mood == selectedMoodFilter
        matchesQuery && matchesMood
    }

    val moodScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .serenityBackground()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Heading: "Journal Archives"
        Text(
            text = "Journal Archives",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your safe space of written reflections & breakthroughs.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                fontSize = 14.sp
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Search Bar Glass Pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search reflections or themes…",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMuted
                            )
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(AccentLavender),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { searchQuery = "" }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mood Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(moodScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" chip
            val isAllSelected = selectedMoodFilter == null
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isAllSelected) AccentLavender else Color(0x18FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        if (isAllSelected) AccentLavender else GlassBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { selectedMoodFilter = null }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isAllSelected) Color(0xFF100D1E) else TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            MoodType.entries.forEach { mood ->
                val isSelected = selectedMoodFilter == mood
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) Color(0x38C9A9E9) else Color(0x18FFFFFF),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) AccentLavender else GlassBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            selectedMoodFilter = if (isSelected) null else mood
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${mood.emoji} ${mood.label}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of Reflections
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reflections match your search.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary
                    )
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { entry ->
                    ReflectionListItem(
                        entry = entry,
                        onClick = { selectedEntryForDetail = entry }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }

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
