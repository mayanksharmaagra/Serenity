package com.jrprofessor.serenity.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.ui.theme.AccentLavender
import com.jrprofessor.serenity.ui.theme.AccentLavenderLight
import com.jrprofessor.serenity.ui.theme.TextPrimary
import com.jrprofessor.serenity.ui.theme.TextSecondary

@Composable
fun MoodFlowChart(
    entries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(entries) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(1200))
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = 18.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: "Mood Flow" and line chart icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Flow",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ShowChart,
                    contentDescription = null,
                    tint = AccentLavender,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                val chartEntries = if (entries.isEmpty()) {
                    emptyList()
                } else {
                    entries.sortedBy { it.timestamp }.takeLast(7)
                }

                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val paddingX = 40f
                    val paddingY = 30f
                    val usableWidth = width - (paddingX * 2)
                    val usableHeight = height - (paddingY * 2)

                    // Draw 3 subtle horizontal background guide lines
                    val gridPaintColor = Color(0x18FFFFFF)
                    for (i in 0..2) {
                        val y = paddingY + (usableHeight * (i / 2f))
                        drawLine(
                            color = gridPaintColor,
                            start = Offset(paddingX, y),
                            end = Offset(width - paddingX, y),
                            strokeWidth = 1.5f
                        )
                    }

                    if (chartEntries.size < 2) {
                        // Single point or empty state
                        return@Canvas
                    }

                    // Map entries to points (x, y)
                    // mood.index: 0 (overwhelmed/lowest) to 4 (joyful/highest)
                    val points = chartEntries.mapIndexed { idx, entry ->
                        val x = paddingX + (idx.toFloat() / (chartEntries.size - 1)) * usableWidth
                        val normalizedMood = entry.mood.index / 4f // 0f to 1f
                        // Flip y since Canvas (0,0) is top-left
                        val y = (paddingY + usableHeight) - (normalizedMood * usableHeight)
                        Offset(x, y)
                    }

                    // Build smooth cubic bezier curve
                    val strokePath = Path()
                    val fillPath = Path()

                    val currentProgress = progress.value
                    val animatedPoints = points.map { pt ->
                        val baselineY = paddingY + (usableHeight / 2f)
                        Offset(pt.x, baselineY + ((pt.y - baselineY) * currentProgress))
                    }

                    strokePath.moveTo(animatedPoints[0].x, animatedPoints[0].y)
                    fillPath.moveTo(animatedPoints[0].x, animatedPoints[0].y)

                    for (i in 0 until animatedPoints.size - 1) {
                        val p0 = animatedPoints[i]
                        val p1 = animatedPoints[i + 1]

                        val cx1 = p0.x + (p1.x - p0.x) / 2f
                        val cy1 = p0.y
                        val cx2 = p0.x + (p1.x - p0.x) / 2f
                        val cy2 = p1.y

                        strokePath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                        fillPath.cubicTo(cx1, cy1, cx2, cy2, p1.x, p1.y)
                    }

                    // Close fill path down to the bottom
                    fillPath.lineTo(animatedPoints.last().x, height)
                    fillPath.lineTo(animatedPoints.first().x, height)
                    fillPath.close()

                    // Draw gradient area under curve
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AccentLavender.copy(alpha = 0.35f * currentProgress),
                                AccentLavender.copy(alpha = 0.08f * currentProgress),
                                Color.Transparent
                            ),
                            startY = paddingY,
                            endY = height
                        )
                    )

                    // Draw glowing outer stroke
                    drawPath(
                        path = strokePath,
                        color = AccentLavender.copy(alpha = 0.4f * currentProgress),
                        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw primary line stroke
                    drawPath(
                        path = strokePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(AccentLavenderLight, AccentLavender, AccentLavenderLight)
                        ),
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw emoji markers at each data point
                    val textPaint = Paint().apply {
                        textSize = 20.dp.toPx()
                        textAlign = Paint.Align.CENTER
                        typeface = Typeface.DEFAULT
                    }

                    animatedPoints.forEachIndexed { idx, pt ->
                        val emoji = chartEntries[idx].mood.emoji
                        drawContext.canvas.nativeCanvas.drawText(
                            emoji,
                            pt.x,
                            pt.y - 12.dp.toPx(),
                            textPaint
                        )
                    }
                }
            }
        }
    }
}
