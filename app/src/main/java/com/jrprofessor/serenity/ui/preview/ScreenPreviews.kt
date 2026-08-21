package com.jrprofessor.serenity.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.CheckInAnalysisResult
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.domain.analyzer.FaceAnalysisState
import com.jrprofessor.serenity.ui.components.AnimatedStressBar
import com.jrprofessor.serenity.ui.components.FaceReactionChip
import com.jrprofessor.serenity.ui.components.GlassCard
import com.jrprofessor.serenity.ui.components.GradientPillButton
import com.jrprofessor.serenity.ui.components.MoodChip
import com.jrprofessor.serenity.ui.components.MoodEmojiSelector
import com.jrprofessor.serenity.ui.components.MoodFlowChart
import com.jrprofessor.serenity.ui.components.OutlinePillButton
import com.jrprofessor.serenity.ui.components.ReflectionListItem
import com.jrprofessor.serenity.ui.components.SegmentedControl
import com.jrprofessor.serenity.ui.components.SerenityHomeTopBar
import com.jrprofessor.serenity.ui.components.SerenityJournalTopBar
import com.jrprofessor.serenity.ui.components.StreakBadge
import com.jrprofessor.serenity.ui.components.SuggestionCard
import com.jrprofessor.serenity.ui.components.ThemeBulletList
import com.jrprofessor.serenity.ui.theme.AccentLavender
import com.jrprofessor.serenity.ui.theme.GlassBorder
import com.jrprofessor.serenity.ui.theme.SerenityTheme
import com.jrprofessor.serenity.ui.theme.TextMuted
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary
import com.jrprofessor.serenity.ui.theme.serenityBackground

// ─── Shared Preview Data ─────────────────────────────────────────────────────

private val previewEntries = listOf(
    MoodEntry(
        mood = MoodType.SAD,
        journalText = "Today was stressful because of the project deadline and back-to-back meetings that drained all my energy.",
        stressLevel = 72,
        themes = listOf("Work pressure", "Mental fatigue", "Low motivation"),
        suggestion = "Take a 15-minute break and step outside if you can.",
        timestamp = System.currentTimeMillis() - 86_400_000L
    ),
    MoodEntry(
        mood = MoodType.GOOD,
        journalText = "Had a calm, productive morning. Finished the feature early and went for a walk.",
        stressLevel = 18,
        themes = listOf("Good progress", "Exercise", "Clear mind"),
        suggestion = "Keep up the morning routine — it's working well for you.",
        timestamp = System.currentTimeMillis() - 172_800_000L
    ),
    MoodEntry(
        mood = MoodType.NEUTRAL,
        journalText = "Nothing special today. Just got through the day with average energy.",
        stressLevel = 55,
        themes = listOf("Routine day", "Low energy"),
        suggestion = "Try something small that brings you joy this evening.",
        timestamp = System.currentTimeMillis() - 259_200_000L
    ),
    MoodEntry(
        mood = MoodType.JOYFUL,
        journalText = "Best day in weeks — celebrated the product launch with the whole team!",
        stressLevel = 8,
        themes = listOf("Achievement", "Team spirit", "Celebration"),
        suggestion = "Savour this energy and share it with someone you care about.",
        timestamp = System.currentTimeMillis() - 345_600_000L
    )
)

private val previewAnalysis = CheckInAnalysisResult(
    stressLevel = 72,
    calmLevel = 28,
    themes = listOf("Work pressure", "Mental fatigue", "Low motivation"),
    suggestion = "Take a 15-minute break and step outside if you can.",
    summarySentence = "You're feeling stressed and mentally drained today."
)

// ─── 1. HOME SCREEN ──────────────────────────────────────────────────────────

@Preview(
    name = "1 · Home Screen",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewHomeScreen() {
    SerenityTheme {
        HomeScreenContent(streakDays = 5)
    }
}

@Composable
private fun HomeScreenContent(streakDays: Int) {
    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(rememberScrollState())
    ) {
        SerenityHomeTopBar(title = "Serenity")
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Good morning ✨",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Take a moment to listen to your inner world.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 15.sp
                )
            )
            Spacer(Modifier.height(20.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                contentPadding = 22.dp,
                borderColor = Color(0x35C9A9E9)
            ) {
                Column {
                    Text(
                        text = "Daily Emotional Check-In",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AccentLavender,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "How are you feeling right now?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Track facial cues with on-device AI and capture your reflections with voice or text.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(Modifier.height(20.dp))
                    GradientPillButton(text = "Begin Check-In", height = 50.dp, onClick = {})
                }
            }
            Spacer(Modifier.height(16.dp))
            StreakBadge(streakDays = streakDays)
            Spacer(Modifier.height(100.dp))
        }
    }
}

// ─── 2. MOOD CHECK-IN SCREEN ─────────────────────────────────────────────────

@Preview(
    name = "2a · Mood Check-In — No Selection",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewMoodCheckInNoSelection() {
    SerenityTheme {
        MoodCheckInContent(selectedMood = null, isFaceScanActive = false)
    }
}

@Preview(
    name = "2b · Mood Check-In — Mood Selected",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewMoodCheckInSelected() {
    SerenityTheme {
        MoodCheckInContent(selectedMood = MoodType.GOOD, isFaceScanActive = false)
    }
}

@Preview(
    name = "2c · Mood Check-In — Face Scan Active",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewMoodCheckInFaceScan() {
    SerenityTheme {
        MoodCheckInContent(selectedMood = MoodType.GOOD, isFaceScanActive = true)
    }
}

@Composable
private fun MoodCheckInContent(selectedMood: MoodType?, isFaceScanActive: Boolean) {
    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(rememberScrollState())
    ) {
        SerenityHomeTopBar(title = "Serenity")
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How are you feeling\ntoday?",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(28.dp))
            MoodEmojiSelector(selectedMood = selectedMood, onMoodSelected = {})
            Spacer(Modifier.height(20.dp))
            OutlinePillButton(
                text = if (isFaceScanActive) "Hide Face Scanner" else "Scan Your Face Instead",
                icon = Icons.Rounded.CameraAlt,
                onClick = {}
            )
            if (isFaceScanActive) {
                Spacer(Modifier.height(24.dp))
                // Static camera card placeholder — real CameraX can't render in @Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(Color(0x2A15102E), RoundedCornerShape(24.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Hold still… · 😊 happy",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "🔒  Processed on your device — not stored or uploaded.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(Modifier.height(28.dp))
            GradientPillButton(text = "Next", enabled = selectedMood != null, onClick = {})
            Spacer(Modifier.height(90.dp))
        }
    }
}

// ─── 3. JOURNAL SCREEN ───────────────────────────────────────────────────────

@Preview(
    name = "3a · Journal — Empty",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewJournalEmpty() {
    SerenityTheme {
        JournalContent(mood = MoodType.SAD, journalText = "", isAnalyzing = false)
    }
}

@Preview(
    name = "3b · Journal — With Text",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewJournalWithText() {
    SerenityTheme {
        JournalContent(
            mood = MoodType.SAD,
            journalText = "Today was stressful because of the project deadline and the back-to-back meetings that drained all my energy.",
            isAnalyzing = false
        )
    }
}

@Preview(
    name = "3c · Journal — Analyzing",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewJournalAnalyzing() {
    SerenityTheme {
        JournalContent(
            mood = MoodType.NEUTRAL,
            journalText = "Quiet day. Nothing much happened.",
            isAnalyzing = true
        )
    }
}

@Composable
private fun JournalContent(mood: MoodType, journalText: String, isAnalyzing: Boolean) {
    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(rememberScrollState())
    ) {
        SerenityJournalTopBar(mood = mood, onBackClick = {})
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = mood.journalPrompt,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    color = TextPrimary
                )
            )
            Spacer(Modifier.height(24.dp))
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(24.dp),
                contentPadding = 20.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (journalText.isEmpty()) {
                            Text(
                                "Let it all out here…",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                            )
                        } else {
                            Text(
                                text = journalText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${journalText.length} / 1000",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        )
                        Text("🎙", fontSize = 22.sp)
                    }
                }
            }
            Spacer(Modifier.height(36.dp))
            GradientPillButton(
                text = "Analyze",
                isLoading = isAnalyzing,
                enabled = journalText.isNotBlank(),
                onClick = {}
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── 4. CHECK-IN RESULT SCREEN ───────────────────────────────────────────────

@Preview(
    name = "4a · Check-In Result — Manual Mood",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewCheckInResultManual() {
    SerenityTheme {
        CheckInResultContent(
            mood = MoodType.SAD,
            faceState = null,
            analysis = previewAnalysis
        )
    }
}

@Preview(
    name = "4b · Check-In Result — Face Scan (Sad)",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewCheckInResultFaceSad() {
    SerenityTheme {
        CheckInResultContent(
            mood = MoodType.SAD,
            faceState = FaceAnalysisState(
                isFaceDetected = true,
                isFaceAligned = true,
                faceScore = 28,
                faceLabel = "sad",
                faceEmoji = "😔"
            ),
            analysis = previewAnalysis
        )
    }
}

@Preview(
    name = "4c · Check-In Result — Joyful",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewCheckInResultJoyful() {
    SerenityTheme {
        CheckInResultContent(
            mood = MoodType.JOYFUL,
            faceState = FaceAnalysisState(
                isFaceDetected = true,
                isFaceAligned = true,
                faceScore = 95,
                faceLabel = "radiant joy",
                faceEmoji = "😄"
            ),
            analysis = CheckInAnalysisResult(
                stressLevel = 8,
                calmLevel = 92,
                themes = listOf("Achievement", "Team spirit", "Celebration"),
                suggestion = "Savour this energy and share it with someone you care about.",
                summarySentence = "You're glowing with joy and achievement today!"
            )
        )
    }
}

@Preview(
    name = "4d · Check-In Result — Anxious / Distressed",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewCheckInResultAnxious() {
    SerenityTheme {
        CheckInResultContent(
            mood = MoodType.OVERWHELMED,
            faceState = FaceAnalysisState(
                isFaceDetected = true,
                isFaceAligned = true,
                faceScore = 14,
                faceLabel = "anxious",
                faceEmoji = "😟"
            ),
            analysis = CheckInAnalysisResult(
                stressLevel = 88,
                calmLevel = 12,
                themes = listOf("Overwhelm", "Racing thoughts", "Burnout"),
                suggestion = "Pause and try 4 deep breaths. You don't have to solve everything right now.",
                summarySentence = "You're experiencing significant emotional distress."
            )
        )
    }
}

@Composable
private fun CheckInResultContent(
    mood: MoodType,
    faceState: FaceAnalysisState?,
    analysis: CheckInAnalysisResult
) {
    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(rememberScrollState())
    ) {
        SerenityHomeTopBar(title = "Serenity", rightIcon = Icons.Rounded.AutoAwesome)
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "Your Check-In",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = TextPrimary
                )
            )
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodChip(mood = mood)
                if (faceState != null) {
                    FaceReactionChip(
                        faceScore = faceState.faceScore,
                        faceLabel = faceState.faceLabel,
                        faceEmoji = faceState.faceEmoji
                    )
                } else {
                    FaceReactionChip(
                        faceScore = if (mood.index >= 3) 82 else 45,
                        faceLabel = if (mood.index >= 3) "content" else "neutral",
                        faceEmoji = if (mood.index >= 3) "😌" else "😐"
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                AnimatedStressBar(stressPercentage = analysis.stressLevel)
            }
            Spacer(Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                ThemeBulletList(themes = analysis.themes)
            }
            Spacer(Modifier.height(16.dp))
            SuggestionCard(suggestion = analysis.suggestion)
            Spacer(Modifier.height(30.dp))
            GradientPillButton(text = "Save to Journal", onClick = {})
            Spacer(Modifier.height(90.dp))
        }
    }
}

// ─── 5. INSIGHTS SCREEN ──────────────────────────────────────────────────────

@Preview(
    name = "5a · Insights — With Data",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewInsightsWithData() {
    SerenityTheme {
        InsightsContent(entries = previewEntries, streakDays = 5, selectedDays = 30)
    }
}

@Preview(
    name = "5b · Insights — Empty State",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewInsightsEmpty() {
    SerenityTheme {
        InsightsContent(entries = emptyList(), streakDays = 0, selectedDays = 7)
    }
}

@Composable
private fun InsightsContent(entries: List<MoodEntry>, streakDays: Int, selectedDays: Int) {
    val daysOptions = listOf(7, 30, 90)
    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(36.dp))
        Text(
            "Your Journey",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = TextPrimary
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Track your emotional landscape over time.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = TextSecondary,
                fontSize = 15.sp
            )
        )
        Spacer(Modifier.height(22.dp))
        SegmentedControl(
            items = daysOptions,
            selectedItem = selectedDays,
            onItemSelected = {},
            labelProvider = { "$it Days" }
        )
        Spacer(Modifier.height(20.dp))
        MoodFlowChart(entries = entries)
        Spacer(Modifier.height(16.dp))
        StreakBadge(streakDays = streakDays)
        Spacer(Modifier.height(24.dp))
        Text(
            "Recent Reflections",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 18.sp
            )
        )
        Spacer(Modifier.height(14.dp))
        if (entries.isEmpty()) {
            Text(
                "No reflections yet. Start your first check-in from the Mood tab!",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                entries.forEach { entry ->
                    ReflectionListItem(entry = entry, onClick = {})
                }
            }
        }
        Spacer(Modifier.height(100.dp))
    }
}

// ─── 6. JOURNAL ARCHIVES SCREEN ──────────────────────────────────────────────

@Preview(
    name = "6a · Journal Archives — With Entries",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewJournalArchivesWithEntries() {
    SerenityTheme {
        JournalArchivesContent(entries = previewEntries)
    }
}

@Preview(
    name = "6b · Journal Archives — Empty",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun PreviewJournalArchivesEmpty() {
    SerenityTheme {
        JournalArchivesContent(entries = emptyList())
    }
}

@Composable
private fun JournalArchivesContent(entries: List<MoodEntry>) {
    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(36.dp))
        Text(
            "Journal Archives",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = TextPrimary
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Your safe space of written reflections & breakthroughs.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                fontSize = 14.sp
            )
        )
        Spacer(Modifier.height(18.dp))
        // Search bar placeholder (BasicTextField can't be shown in @Preview easily)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1AFFFFFF), RoundedCornerShape(22.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 13.dp)
        ) {
            Text(
                "🔍  Search reflections or themes…",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
            )
        }
        Spacer(Modifier.height(12.dp))
        // Mood filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All" to true, "😭" to false, "😔" to false, "😐" to false, "🙂" to false, "😍" to false)
                .forEach { (label, isSelected) ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) AccentLavender else Color(0x18FFFFFF),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) AccentLavender else GlassBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color(0xFF100D1E) else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
        }
        Spacer(Modifier.height(16.dp))
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No reflections yet. Start your first check-in!",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                entries.forEach { entry ->
                    ReflectionListItem(entry = entry, onClick = {})
                }
            }
        }
        Spacer(Modifier.height(90.dp))
    }
}

// ─── 7. COMPONENT PREVIEWS ───────────────────────────────────────────────────

@Preview(name = "7a · MoodEmojiSelector — None", showBackground = true, widthDp = 420)
@Composable
private fun PreviewMoodSelectorNone() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            MoodEmojiSelector(selectedMood = null, onMoodSelected = {})
        }
    }
}

@Preview(name = "7b · MoodEmojiSelector — Joyful", showBackground = true, widthDp = 420)
@Composable
private fun PreviewMoodSelectorJoyful() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            MoodEmojiSelector(selectedMood = MoodType.JOYFUL, onMoodSelected = {})
        }
    }
}

@Preview(name = "7c · StreakBadge — 12 days", showBackground = true, widthDp = 420)
@Composable
private fun PreviewStreakBadge() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            StreakBadge(streakDays = 12)
        }
    }
}

@Preview(name = "7d · StressBar — High (78%)", showBackground = true, widthDp = 420)
@Composable
private fun PreviewStressBarHigh() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                AnimatedStressBar(stressPercentage = 78)
            }
        }
    }
}

@Preview(name = "7e · StressBar — Low/Calm (12%)", showBackground = true, widthDp = 420)
@Composable
private fun PreviewStressBarLow() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                AnimatedStressBar(stressPercentage = 12)
            }
        }
    }
}

@Preview(name = "7f · FaceReactionChip — 😔 Sad", showBackground = true, widthDp = 420)
@Composable
private fun PreviewFaceReactionSad() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            FaceReactionChip(faceScore = 28, faceLabel = "sad", faceEmoji = "😔")
        }
    }
}

@Preview(name = "7g · FaceReactionChip — 😄 Radiant Joy", showBackground = true, widthDp = 420)
@Composable
private fun PreviewFaceReactionJoy() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            FaceReactionChip(faceScore = 95, faceLabel = "radiant joy", faceEmoji = "😄")
        }
    }
}

@Preview(name = "7h · FaceReactionChip — 😟 Anxious", showBackground = true, widthDp = 420)
@Composable
private fun PreviewFaceReactionAnxious() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            FaceReactionChip(faceScore = 14, faceLabel = "anxious", faceEmoji = "😟")
        }
    }
}

@Preview(name = "7i · ThemeBulletList", showBackground = true, widthDp = 420)
@Composable
private fun PreviewThemeBulletList() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
                ThemeBulletList(themes = listOf("Work pressure", "Mental fatigue", "Low motivation"))
            }
        }
    }
}

@Preview(name = "7j · SuggestionCard", showBackground = true, widthDp = 420)
@Composable
private fun PreviewSuggestionCard() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            SuggestionCard(suggestion = "Take a 15-minute break and step outside if you can.")
        }
    }
}

@Preview(name = "7k · MoodFlowChart — 4 entries", showBackground = true, widthDp = 420)
@Composable
private fun PreviewMoodFlowChart() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            MoodFlowChart(entries = previewEntries)
        }
    }
}

@Preview(name = "7l · ReflectionListItem — Stress", showBackground = true, widthDp = 420)
@Composable
private fun PreviewReflectionItemStress() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(16.dp)) {
            ReflectionListItem(entry = previewEntries[0], onClick = {})
        }
    }
}

@Preview(name = "7m · ReflectionListItem — Calm", showBackground = true, widthDp = 420)
@Composable
private fun PreviewReflectionItemCalm() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(16.dp)) {
            ReflectionListItem(entry = previewEntries[1], onClick = {})
        }
    }
}

@Preview(name = "7n · GradientPillButton — Enabled", showBackground = true, widthDp = 420)
@Composable
private fun PreviewButtonEnabled() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            GradientPillButton(text = "Begin Check-In", onClick = {})
        }
    }
}

@Preview(name = "7o · GradientPillButton — Disabled", showBackground = true, widthDp = 420)
@Composable
private fun PreviewButtonDisabled() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            GradientPillButton(text = "Next", enabled = false, onClick = {})
        }
    }
}

@Preview(name = "7p · GradientPillButton — Loading", showBackground = true, widthDp = 420)
@Composable
private fun PreviewButtonLoading() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            GradientPillButton(text = "Analyze", isLoading = true, onClick = {})
        }
    }
}

@Preview(name = "7q · SegmentedControl — 30 Days selected", showBackground = true, widthDp = 420)
@Composable
private fun PreviewSegmentedControl() {
    SerenityTheme {
        Box(Modifier.background(Color(0xFF12101F)).padding(20.dp)) {
            SegmentedControl(
                items = listOf(7, 30, 90),
                selectedItem = 30,
                onItemSelected = {},
                labelProvider = { "$it Days" }
            )
        }
    }
}
