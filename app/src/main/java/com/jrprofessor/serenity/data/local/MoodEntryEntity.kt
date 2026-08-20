package com.jrprofessor.serenity.data.local

import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.model.MoodType

data class MoodEntryEntity(
    val id: String,
    val timestamp: Long,
    val moodIndex: Int,
    val moodSource: String,
    val faceScore: Int?,
    val faceLabel: String?,
    val journalText: String,
    val stressLevel: Int,
    val themesString: String, // Comma separated themes
    val suggestion: String
) {
    fun toDomain(): MoodEntry {
        val themes = if (themesString.isBlank()) {
            emptyList()
        } else {
            themesString.split("|||").map { it.trim() }.filter { it.isNotEmpty() }
        }
        return MoodEntry(
            id = id,
            timestamp = timestamp,
            mood = MoodType.fromIndex(moodIndex),
            moodSource = moodSource,
            faceScore = faceScore,
            faceLabel = faceLabel,
            journalText = journalText,
            stressLevel = stressLevel,
            themes = themes,
            suggestion = suggestion
        )
    }

    companion object {
        fun fromDomain(domain: MoodEntry): MoodEntryEntity {
            return MoodEntryEntity(
                id = domain.id,
                timestamp = domain.timestamp,
                moodIndex = domain.mood.index,
                moodSource = domain.moodSource,
                faceScore = domain.faceScore,
                faceLabel = domain.faceLabel,
                journalText = domain.journalText,
                stressLevel = domain.stressLevel,
                themesString = domain.themes.joinToString("|||"),
                suggestion = domain.suggestion
            )
        }
    }
}
