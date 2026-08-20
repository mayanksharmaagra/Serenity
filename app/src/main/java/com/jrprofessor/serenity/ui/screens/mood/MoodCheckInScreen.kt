package com.jrprofessor.serenity.ui.screens.mood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.ui.components.FaceScanCameraCard
import com.jrprofessor.serenity.ui.components.GradientPillButton
import com.jrprofessor.serenity.ui.components.MoodEmojiSelector
import com.jrprofessor.serenity.ui.components.OutlinePillButton
import com.jrprofessor.serenity.ui.components.SerenityHomeTopBar
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.serenityBackground

@Composable
fun MoodCheckInScreen(
    viewModel: MoodViewModel,
    onNavigateToJournal: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val selectedMood by viewModel.selectedMood.collectAsState()
    val isFaceScanActive by viewModel.isFaceScanActive.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .serenityBackground()
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        SerenityHomeTopBar(
            title = "Serenity",
            onProfileClick = onNavigateToProfile,
            onSettingsClick = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Heading
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

            Spacer(modifier = Modifier.height(28.dp))

            // 5 Mood Emoji Selector Row
            MoodEmojiSelector(
                selectedMood = selectedMood,
                onMoodSelected = { mood ->
                    viewModel.selectMood(mood)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Secondary CTA: "Scan Your Face Instead"
            OutlinePillButton(
                text = if (isFaceScanActive) "Hide Face Scanner" else "Scan Your Face Instead",
                icon = Icons.Rounded.CameraAlt,
                onClick = {
                    viewModel.toggleFaceScan()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Camera Card Preview & Oval Overlay
            FaceScanCameraCard(
                isScanningActive = isFaceScanActive,
                onFaceAnalysisUpdated = { state ->
                    viewModel.updateFaceAnalysis(state)
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary CTA: "Next"
            GradientPillButton(
                text = "Next",
                enabled = selectedMood != null,
                onClick = onNavigateToJournal
            )

            Spacer(modifier = Modifier.height(90.dp)) // Padding for bottom bar
        }
    }
}
