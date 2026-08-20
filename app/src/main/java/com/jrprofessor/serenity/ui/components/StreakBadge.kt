package com.jrprofessor.serenity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.ui.theme.AccentMint
import com.jrprofessor.serenity.ui.theme.CalmPillBg
import com.jrprofessor.serenity.ui.theme.CalmPillText
import com.jrprofessor.serenity.ui.theme.GlassBorder
import com.jrprofessor.serenity.ui.theme.StressPillBg
import com.jrprofessor.serenity.ui.theme.StressPillText
import com.jrprofessor.serenity.ui.theme.TextMuted
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        contentPadding = 16.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$streakDays-day journaling streak",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AccentMint,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun ReflectionListItem(
    entry: MoodEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        contentPadding = 16.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Date & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.formattedDateShort,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                )

                // Status Pill (Stress % vs Calm %)
                StatusPill(
                    isCalm = entry.isCalm,
                    percentage = if (entry.isCalm) entry.calmPercentage else entry.stressLevel
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body Row: Mood Emoji & Snippet
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.mood.emoji,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = entry.snippet,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    ),
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatusPill(
    isCalm: Boolean,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val bgColor = if (isCalm) CalmPillBg else StressPillBg
    val textColor = if (isCalm) CalmPillText else StressPillText
    val icon = if (isCalm) "🌿 Calm" else "💧 Stress"

    Box(
        modifier = modifier
            .clip(shape)
            .background(bgColor, shape)
            .border(1.dp, textColor.copy(alpha = 0.3f), shape)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$icon $percentage%",
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        )
    }
}
