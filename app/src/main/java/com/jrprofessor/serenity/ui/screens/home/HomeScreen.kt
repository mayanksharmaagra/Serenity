package com.jrprofessor.serenity.ui.screens.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.ui.components.GlassCard
import com.jrprofessor.serenity.ui.components.GradientPillButton
import com.jrprofessor.serenity.ui.components.SerenityHomeTopBar
import com.jrprofessor.serenity.ui.components.StreakBadge
import com.jrprofessor.serenity.ui.screens.insights.InsightsViewModel
import com.jrprofessor.serenity.ui.theme.AccentCoral
import com.jrprofessor.serenity.ui.theme.AccentLavender
import com.jrprofessor.serenity.ui.theme.AccentMint
import com.jrprofessor.serenity.ui.theme.BgGradientEnd
import com.jrprofessor.serenity.ui.theme.BgGradientMid
import com.jrprofessor.serenity.ui.theme.GlassBorder
import com.jrprofessor.serenity.ui.theme.PrimaryButtonBrush
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary
import com.jrprofessor.serenity.ui.theme.serenityBackground
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    insightsViewModel: InsightsViewModel,
    onStartCheckIn: () -> Unit,
    onNavigateToInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streakDays by insightsViewModel.currentStreak.collectAsState()
    val scrollState = rememberScrollState()

    var showBreatheDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Peaceful night"
        }
    }

    Column(
        modifier = modifier
            .serenityBackground()
            .verticalScroll(scrollState)
    ) {
        // Top Bar
        SerenityHomeTopBar(
            title = "Serenity"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Greeting Header
            Text(
                text = "$greeting ✨",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Take a moment to listen to your inner world.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Card: Check-In CTA
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                contentPadding = 22.dp,
                borderColor = Color(0x35C9A9E9)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Emotional Check-In",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = AccentLavender,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "How are you feeling right now?",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                            )
                        }

                        Text(
                            text = "✨",
                            fontSize = 32.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Track facial cues with on-device AI and capture your reflections with voice or text.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    GradientPillButton(
                        text = "Begin Check-In",
                        height = 50.dp,
                        onClick = onStartCheckIn
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Badge
            StreakBadge(
                streakDays = streakDays
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mindfulness Tools Title
            Text(
                text = "Mindfulness Tools",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Two Quick Action Mini-Cards: "Meditate & Breathe" & "Wellness Guide"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Meditate & Breathe
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = 18.dp,
                    onClick = { showBreatheDialog = true }
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x307FE0B4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Spa,
                                contentDescription = null,
                                tint = AccentMint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Breathe & Meditate",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "4-7-8 Box pacing",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Card 2: Wellness Guide
                GlassCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = 18.dp,
                    onClick = { showGuideDialog = true }
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x30C9A9E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                                contentDescription = null,
                                tint = AccentLavender,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Wellness Guide",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mind grounding tips",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
        }
    }

    // Interactive Box Breathing Dialog
    if (showBreatheDialog) {
        BasicAlertDialog(onDismissRequest = { showBreatheDialog = false }) {
            BreatheMeditationDialogContent(onClose = { showBreatheDialog = false })
        }
    }

    // Wellness Guide Dialog
    if (showGuideDialog) {
        BasicAlertDialog(onDismissRequest = { showGuideDialog = false }) {
            WellnessGuideDialogContent(onClose = { showGuideDialog = false })
        }
    }
}

@Composable
fun BreatheMeditationDialogContent(onClose: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathe_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_scale"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        backgroundColor = BgGradientMid,
        contentPadding = 24.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4-7-8 Breathing Space",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Animated Breathing Circle
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(scale)
                        .shadow(20.dp, CircleShape, spotColor = AccentMint)
                        .clip(CircleShape)
                        .background(Color(0x337FE0B4))
                        .border(2.dp, AccentMint, CircleShape)
                )

                Text(
                    text = if (scale > 1.05f) "Inhale Slowly" else "Exhale Gently",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Inhale through your nose, hold your breath, then exhale gently through your mouth to calm your heart rate.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            GradientPillButton(
                text = "Done",
                height = 46.dp,
                onClick = onClose
            )
        }
    }
}

@Composable
fun WellnessGuideDialogContent(onClose: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        backgroundColor = BgGradientMid,
        contentPadding = 24.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emotional Reset Guide",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "1. Name the Emotion: Identifying exact feelings disarms amygdala overwhelm.\n\n2. 5-4-3-2-1 Sensory Grounding: Notice 5 sights, 4 physical sensations, 3 sounds, 2 scents, and 1 positive affirmation.\n\n3. Micro-Reflection: Write freely without editing your words — release without judgment.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary.copy(alpha = 0.9f),
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            GradientPillButton(
                text = "Got it",
                height = 46.dp,
                onClick = onClose
            )
        }
    }
}
