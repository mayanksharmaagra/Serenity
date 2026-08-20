package com.jrprofessor.serenity.ui.screens.mood

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.domain.speech.SpeechToTextManager
import com.jrprofessor.serenity.ui.components.GlassCard
import com.jrprofessor.serenity.ui.components.GradientPillButton
import com.jrprofessor.serenity.ui.components.SerenityJournalTopBar
import com.jrprofessor.serenity.ui.theme.AccentCoral
import com.jrprofessor.serenity.ui.theme.AccentLavender
import com.jrprofessor.serenity.ui.theme.GlassBorder
import com.jrprofessor.serenity.ui.theme.TextMuted
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary
import com.jrprofessor.serenity.ui.theme.serenityBackground

@Composable
fun JournalScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val selectedMood by viewModel.selectedMood.collectAsState()
    val journalText by viewModel.journalText.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    val currentMood = selectedMood ?: MoodType.NEUTRAL
    val context = LocalContext.current

    // Speech Recognition
    val speechManager = remember { SpeechToTextManager(context) }
    val isListening by speechManager.isListening.collectAsState()
    val spokenText by speechManager.spokenText.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            speechManager.startListening()
        }
    }

    LaunchedEffect(spokenText) {
        if (spokenText.isNotBlank()) {
            viewModel.appendSpokenText(spokenText)
            speechManager.clearSpokenText()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechManager.stopListening()
        }
    }

    val scrollState = rememberScrollState()

    // Mic pulsing animation when recording
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Column(
        modifier = Modifier
            .serenityBackground()
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        // Top Bar with back arrow & Mood Chip
        SerenityJournalTopBar(
            mood = currentMood,
            onBackClick = onNavigateBack
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Dynamic Mood Prompt Heading (Stitch Screen 2)
            Text(
                text = currentMood.journalPrompt,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Multiline Journal Input Glass Card
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
                                text = "Let it all out here…",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                            )
                        }

                        BasicTextField(
                            value = journalText,
                            onValueChange = { viewModel.updateJournalText(it) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = TextPrimary,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            ),
                            cursorBrush = SolidColor(AccentLavender),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Bottom Row inside Input Area: Counter & Mic Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Character Counter (e.g. 0 / 1000)
                        Text(
                            text = "${journalText.length} / 1000",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        )

                        // Voice-to-text Mic Button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .scale(if (isListening) micPulseScale else 1f)
                                .shadow(
                                    elevation = if (isListening) 12.dp else 4.dp,
                                    shape = CircleShape,
                                    spotColor = if (isListening) AccentCoral else AccentLavender
                                )
                                .clip(CircleShape)
                                .background(
                                    if (isListening) Color(0x66FF6F61) else Color(0x28C9A9E9),
                                    CircleShape
                                )
                                .border(
                                    1.5.dp,
                                    if (isListening) AccentCoral else GlassBorder,
                                    CircleShape
                                )
                                .clickable {
                                    if (isListening) {
                                        speechManager.stopListening()
                                    } else {
                                        if (hasAudioPermission) {
                                            speechManager.startListening()
                                        } else {
                                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                                contentDescription = "Voice Input",
                                tint = if (isListening) AccentCoral else TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Primary CTA: "Analyze"
            GradientPillButton(
                text = "Analyze",
                isLoading = isAnalyzing,
                onClick = {
                    viewModel.analyzeCheckIn(onComplete = onNavigateToResult)
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
