package com.jrprofessor.serenity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.ui.theme.GlassBorder
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary

@Composable
fun MoodChip(
    mood: MoodType,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0x281B1536), shape)
            .border(1.dp, GlassBorder, shape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mood:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = mood.emoji,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun FaceReactionChip(
    faceScore: Int,
    faceLabel: String,
    faceEmoji: String = "",
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(0x281B1536), shape)
            .border(1.dp, GlassBorder, shape)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Face:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (faceEmoji.isNotBlank()) {
                Text(
                    text = faceEmoji,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "$faceLabel · $faceScore/100",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
