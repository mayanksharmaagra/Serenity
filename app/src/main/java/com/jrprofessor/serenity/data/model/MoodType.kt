package com.jrprofessor.serenity.data.model

enum class MoodType(
    val index: Int,
    val emoji: String,
    val label: String,
    val journalPrompt: String,
    val defaultStressLevel: Int
) {
    OVERWHELMED(
        index = 0,
        emoji = "😭",
        label = "Overwhelmed",
        journalPrompt = "Today was overwhelming because…",
        defaultStressLevel = 88
    ),
    SAD(
        index = 1,
        emoji = "😔",
        label = "Down / Stressed",
        journalPrompt = "Today was stressful because…",
        defaultStressLevel = 72
    ),
    NEUTRAL(
        index = 2,
        emoji = "😐",
        label = "Neutral",
        journalPrompt = "Today was quiet because…",
        defaultStressLevel = 45
    ),
    GOOD(
        index = 3,
        emoji = "🙂",
        label = "Good",
        journalPrompt = "Today was uplifting because…",
        defaultStressLevel = 22
    ),
    JOYFUL(
        index = 4,
        emoji = "😍",
        label = "Joyful",
        journalPrompt = "Today was wonderful because…",
        defaultStressLevel = 10
    );

    companion object {
        fun fromIndex(index: Int): MoodType {
            return entries.find { it.index == index } ?: NEUTRAL
        }
    }
}
