package com.jrprofessor.serenity.domain.analyzer

import com.jrprofessor.serenity.data.model.CheckInAnalysisResult
import com.jrprofessor.serenity.data.model.MoodType

object MoodJournalAnalyzer {

    // Keywords mapping to themes
    private val themeKeywords = mapOf(
        "Work pressure" to listOf("work", "job", "deadline", "boss", "manager", "meeting", "client", "project", "presentation", "tasks", "overtime", "career", "office", "code", "bug", "tickets"),
        "Mental fatigue" to listOf("tired", "exhausted", "drain", "drained", "brain fog", "burnout", "overload", "headache", "heavy", "sleepy", "zombie", "can't think"),
        "Low motivation" to listOf("unmotivated", "procrastinating", "stuck", "lazy", "no drive", "hard to start", "meaningless", "pointless", "no energy", "sluggish"),
        "Relationship stress" to listOf("fight", "argued", "argument", "partner", "spouse", "friend", "family", "mom", "dad", "breakup", "lonely", "misunderstood", "conflict"),
        "Emotional overwhelm" to listOf("crying", "anxious", "anxiety", "panic", "fear", "scared", "overwhelmed", "hopeless", "drowning", "too much", "nervous", "shaking"),
        "Physical tension" to listOf("sore", "pain", "back", "neck", "insomnia", "sick", "ache", "tight", "stomach", "tension"),
        "Quiet gratitude" to listOf("grateful", "thankful", "blessed", "appreciation", "kindness", "gentle", "peaceful", "sunset", "nature", "walk", "coffee", "smile"),
        "Social connection" to listOf("friends", "dinner", "laughed", "together", "party", "call", "conversation", "bonding", "connected", "loved"),
        "Creative flow" to listOf("inspired", "created", "writing", "music", "art", "ideas", "excited", "progress", "breakthrough", "momentum")
    )

    private val stressKeywords = listOf(
        "stress", "stressed", "overwhelmed", "deadline", "panic", "anxious", "exhausted",
        "pressure", "crying", "awful", "terrible", "burnout", "furious", "angry", "chaos"
    )

    private val calmKeywords = listOf(
        "calm", "peace", "peaceful", "relaxed", "happy", "joy", "grateful", "rested",
        "satisfied", "smooth", "lovely", "content", "breeze", "breathe", "centered"
    )

    fun analyze(
        mood: MoodType,
        faceScore: Int?,
        faceLabel: String?,
        journalText: String
    ): CheckInAnalysisResult {
        val lowerText = journalText.lowercase()

        // 1. Calculate Base Stress Score from selected mood
        var stress = mood.defaultStressLevel.toDouble()

        // 2. Adjust with Face Scan if available
        if (faceScore != null) {
            // faceScore is 0-100 where 100 = very happy/calm, 0 = very sad/distressed
            val faceDerivedStress = (100 - faceScore).toDouble()
            stress = (stress * 0.65) + (faceDerivedStress * 0.35)
        }

        // 3. Adjust with NLP Text Sentiment Keywords
        var textModifier = 0
        for (kw in stressKeywords) {
            if (lowerText.contains(kw)) textModifier += 6
        }
        for (kw in calmKeywords) {
            if (lowerText.contains(kw)) textModifier -= 6
        }
        stress += textModifier.coerceIn(-25, 25)

        // Clamp stress between 5 and 98
        val finalStressLevel = stress.toInt().coerceIn(5, 98)
        val finalCalmLevel = 100 - finalStressLevel

        // 4. Extract Detected Themes
        val detectedThemes = mutableListOf<String>()
        for ((theme, keywords) in themeKeywords) {
            if (keywords.any { kw -> lowerText.contains(kw) }) {
                detectedThemes.add(theme)
            }
        }

        // Fallbacks if no specific keywords were typed
        if (detectedThemes.isEmpty()) {
            when (mood) {
                MoodType.OVERWHELMED -> detectedThemes.addAll(listOf("Emotional overwhelm", "Mental fatigue", "High stress"))
                MoodType.SAD -> detectedThemes.addAll(listOf("Work pressure", "Mental fatigue", "Low motivation"))
                MoodType.NEUTRAL -> detectedThemes.addAll(listOf("Daily routine", "Quiet reflections"))
                MoodType.GOOD -> detectedThemes.addAll(listOf("Positive momentum", "Peace of mind"))
                MoodType.JOYFUL -> detectedThemes.addAll(listOf("Quiet gratitude", "Creative flow", "Social connection"))
            }
        }

        val topThemes = detectedThemes.take(3)

        // 5. Generate Tailored Actionable Suggestion
        val suggestion = generateSuggestion(finalStressLevel, topThemes, mood)
        val summary = generateSummary(finalStressLevel, topThemes)

        return CheckInAnalysisResult(
            stressLevel = finalStressLevel,
            calmLevel = finalCalmLevel,
            themes = topThemes,
            suggestion = suggestion,
            summarySentence = summary
        )
    }

    private fun generateSuggestion(stressLevel: Int, themes: List<String>, mood: MoodType): String {
        return when {
            themes.contains("Work pressure") && stressLevel > 60 ->
                "Take a 15-minute break and step outside if you can. Stepping away helps reset cortisol spikes."
            themes.contains("Mental fatigue") || themes.contains("Sleep deprivation") ->
                "Give your mind an intentional pause: dim your screen, close your eyes for 3 minutes, and drink a glass of water."
            themes.contains("Low motivation") ->
                "Break down your next task into an ultra-small 2-minute step. Action often creates motivation, not vice-versa."
            themes.contains("Emotional overwhelm") ->
                "Try a 4-7-8 breathing exercise: inhale for 4s, hold for 7s, and exhale for 8s to calm your central nervous system."
            themes.contains("Relationship stress") ->
                "Give yourself space before responding. Writing your raw thoughts in private was a healthy first step."
            themes.contains("Quiet gratitude") || mood == MoodType.JOYFUL ->
                "Anchoring this joy now will build emotional resilience for tomorrow. Take a mental snapshot of how this feels."
            stressLevel < 35 ->
                "You are in a great headspace today. Channel this steady clarity into something meaningful to you."
            else ->
                "Honor how you feel right now. You don't have to fix everything today — just one mindful breath at a time."
        }
    }

    private fun generateSummary(stressLevel: Int, themes: List<String>): String {
        val themeNames = themes.joinToString(", ")
        return if (stressLevel > 50) {
            "Experiencing heightened tension related to $themeNames."
        } else {
            "Experiencing a centered, calm state anchored by $themeNames."
        }
    }
}
