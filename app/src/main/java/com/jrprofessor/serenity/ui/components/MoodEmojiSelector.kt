package com.jrprofessor.serenity.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.ui.theme.AccentYellow

@Composable
fun MoodEmojiSelector(
    selectedMood: MoodType?,
    onMoodSelected: (MoodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoodType.entries.forEach { mood ->
            val isSelected = selectedMood == mood

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.28f else 0.90f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "mood_scale_${mood.name}"
            )

            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1.0f else (if (selectedMood == null) 0.85f else 0.40f),
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "mood_alpha_${mood.name}"
            )

            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .scale(scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onMoodSelected(mood) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Glowing outer ring for selected mood
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                spotColor = AccentYellow,
                                ambientColor = AccentYellow
                            )
                            .clip(CircleShape)
                            .background(Color(0x33FFC94A))
                            .border(2.5.dp, AccentYellow, CircleShape)
                    )
                }

                // Emoji Icon
                Text(
                    text = mood.emoji,
                    fontSize = if (isSelected) 32.sp else 28.sp,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}
