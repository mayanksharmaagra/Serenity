package com.jrprofessor.serenity

import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.domain.analyzer.MoodJournalAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoodJournalAnalyzerTest {

    @Test
    fun analyze_stressfulMoodWithWorkPressure_detectsHighStressAndThemes() {
        val result = MoodJournalAnalyzer.analyze(
            mood = MoodType.SAD,
            faceScore = 68,
            faceLabel = "mostly sad",
            journalText = "Work deadline is approaching and my manager added three more tickets. Feeling totally drained and exhausted."
        )

        assertTrue("Stress level should be elevated (> 60)", result.stressLevel > 60)
        assertTrue("Themes should include Work pressure", result.themes.contains("Work pressure"))
        assertTrue("Themes should include Mental fatigue", result.themes.contains("Mental fatigue"))
        assertTrue("Suggestion should be populated", result.suggestion.isNotBlank())
    }

    @Test
    fun analyze_joyfulMoodWithGratitude_detectsLowStressAndPositiveThemes() {
        val result = MoodJournalAnalyzer.analyze(
            mood = MoodType.JOYFUL,
            faceScore = 95,
            faceLabel = "radiant smile",
            journalText = "Had a peaceful morning walk in nature. So grateful for my friends and family."
        )

        assertTrue("Stress level should be low (< 35)", result.stressLevel < 35)
        assertTrue("Calm level should be high (> 65)", result.calmLevel > 65)
        assertTrue("Themes should include Quiet gratitude", result.themes.contains("Quiet gratitude"))
    }

    @Test
    fun analyze_neutralMoodFallback_returnsValidResult() {
        val result = MoodJournalAnalyzer.analyze(
            mood = MoodType.NEUTRAL,
            faceScore = null,
            faceLabel = null,
            journalText = ""
        )

        assertEquals(MoodType.NEUTRAL.defaultStressLevel, result.stressLevel)
        assertTrue(result.themes.isNotEmpty())
        assertTrue(result.suggestion.isNotBlank())
    }
}
