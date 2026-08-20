package com.jrprofessor.serenity.ui.screens.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.ui.components.AnimatedStressBar
import com.jrprofessor.serenity.ui.components.FaceReactionChip
import com.jrprofessor.serenity.ui.components.GlassCard
import com.jrprofessor.serenity.ui.components.MoodChip
import com.jrprofessor.serenity.ui.components.StatusPill
import com.jrprofessor.serenity.ui.components.SuggestionCard
import com.jrprofessor.serenity.ui.components.ThemeBulletList
import com.jrprofessor.serenity.ui.theme.AccentCoral
import com.jrprofessor.serenity.ui.theme.BgGradientEnd
import com.jrprofessor.serenity.ui.theme.BgGradientMid
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionDetailSheet(
    entry: MoodEntry,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BgGradientMid,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            // Header Row: Full Date + Delete Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.formattedDateFull,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete entry",
                        tint = AccentCoral
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chips Row: Mood & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodChip(mood = entry.mood)

                StatusPill(
                    isCalm = entry.isCalm,
                    percentage = if (entry.isCalm) entry.calmPercentage else entry.stressLevel
                )
            }

            if (entry.faceScore != null && entry.faceLabel != null) {
                Spacer(modifier = Modifier.height(10.dp))
                FaceReactionChip(
                    faceScore = entry.faceScore,
                    faceLabel = entry.faceLabel
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Journal Entry Text Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Journal Reflection",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.journalText.ifBlank { "No text entered for this check-in." },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stress Bar Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                AnimatedStressBar(stressPercentage = entry.stressLevel)
            }

            if (entry.themes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    ThemeBulletList(themes = entry.themes)
                }
            }

            if (entry.suggestion.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                SuggestionCard(suggestion = entry.suggestion)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
