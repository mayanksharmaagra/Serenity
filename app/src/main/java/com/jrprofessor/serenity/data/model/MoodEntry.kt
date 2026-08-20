package com.jrprofessor.serenity.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val mood: MoodType,
    val moodSource: String = "manual", // "manual" | "face_scan"
    val faceScore: Int? = null,
    val faceLabel: String? = null,
    val journalText: String,
    val stressLevel: Int, // 0-100
    val themes: List<String>,
    val suggestion: String
) {
    val isCalm: Boolean
        get() = stressLevel < 50

    val calmPercentage: Int
        get() = (100 - stressLevel).coerceIn(0, 100)

    val formattedDateShort: String
        get() {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            return sdf.format(Date(timestamp)).uppercase()
        }

    val formattedDateFull: String
        get() {
            val sdf = SimpleDateFormat("MMMM d, yyyy · h:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    val snippet: String
        get() = if (journalText.length > 85) {
            journalText.take(82) + "..."
        } else {
            journalText
        }
}
