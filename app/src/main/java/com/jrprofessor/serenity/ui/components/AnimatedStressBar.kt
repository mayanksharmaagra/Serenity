package com.jrprofessor.serenity.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.ui.theme.AccentCoral
import com.jrprofessor.serenity.ui.theme.AccentMint
import com.jrprofessor.serenity.ui.theme.CalmBarBrush
import com.jrprofessor.serenity.ui.theme.StressBarBrush
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary

@Composable
fun AnimatedStressBar(
    stressPercentage: Int,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val curPercent = (stressPercentage.coerceIn(0, 100) / 100f)

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) curPercent else 0f,
        animationSpec = tween(durationMillis = 1100, delayMillis = 150),
        label = "stress_bar_progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val isCalm = stressPercentage < 50
    val barBrush = if (isCalm) CalmBarBrush else StressBarBrush
    val labelColor = if (isCalm) AccentMint else AccentCoral

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isCalm) "Calm & Balance Level" else "Stress Level",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Track & Fill bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0x280E0B1F))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(barBrush)
                )
            }

            // Percentage text
            Text(
                text = "$stressPercentage%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = labelColor
                )
            )
        }
    }
}
