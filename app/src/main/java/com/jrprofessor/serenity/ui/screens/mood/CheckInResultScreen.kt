package com.jrprofessor.serenity.ui.screens.mood

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.ui.components.AnimatedStressBar
import com.jrprofessor.serenity.ui.components.FaceReactionChip
import com.jrprofessor.serenity.ui.components.GlassCard
import com.jrprofessor.serenity.ui.components.GradientPillButton
import com.jrprofessor.serenity.ui.components.MoodChip
import com.jrprofessor.serenity.ui.components.SerenityHomeTopBar
import com.jrprofessor.serenity.ui.components.SuggestionCard
import com.jrprofessor.serenity.ui.components.ThemeBulletList
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.serenityBackground

@Composable
fun CheckInResultScreen(
    viewModel: MoodViewModel,
    onSaveSuccess: () -> Unit
) {
    val selectedMood by viewModel.selectedMood.collectAsState()
    val faceState by viewModel.faceAnalysisState.collectAsState()
    val isFaceScanActive by viewModel.isFaceScanActive.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()

    val currentMood = selectedMood ?: MoodType.NEUTRAL
    val analysis = analysisResult

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(verticalScroll)
    ) {
        // Top Bar
        SerenityHomeTopBar(
            title = "Serenity",
            rightIcon = Icons.Rounded.AutoAwesome
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Heading: "Your Check-In"
            Text(
                text = "Your Check-In",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Chip Row: Mood & Face Reaction
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodChip(mood = currentMood)

                if (isFaceScanActive && faceState.isFaceDetected) {
                    FaceReactionChip(
                        faceScore = faceState.faceScore,
                        faceLabel = faceState.faceLabel
                    )
                } else {
                    FaceReactionChip(
                        faceScore = if (currentMood.index >= 3) 82 else 45,
                        faceLabel = if (currentMood.index >= 3) "mostly calm" else "mostly stressed"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card 1: Stress Level Bar
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 20.dp
            ) {
                AnimatedStressBar(
                    stressPercentage = analysis?.stressLevel ?: currentMood.defaultStressLevel
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Themes Bullet List
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 20.dp
            ) {
                ThemeBulletList(
                    themes = analysis?.themes ?: listOf(
                        "Work pressure",
                        "Mental fatigue",
                        "Low motivation"
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Suggestion Card
            SuggestionCard(
                suggestion = analysis?.suggestion
                    ?: "Take a 15-minute break and step outside if you can."
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Primary CTA: "Save to Journal"
            GradientPillButton(
                text = "Save to Journal",
                onClick = {
                    viewModel.saveCheckIn(onSaved = onSaveSuccess)
                }
            )

            Spacer(modifier = Modifier.height(90.dp)) // padding for bottom bar
        }
    }
}
