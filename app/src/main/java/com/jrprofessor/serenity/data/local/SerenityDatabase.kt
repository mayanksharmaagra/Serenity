package com.jrprofessor.serenity.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.model.MoodType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SerenityDatabase private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    "serenity_mood_db",
    null,
    1
) {
    private val dao = SQLiteMoodDao(this)

    fun moodDao(): MoodDao = dao

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS mood_entries (
                id TEXT PRIMARY KEY,
                timestamp INTEGER NOT NULL,
                moodIndex INTEGER NOT NULL,
                moodSource TEXT NOT NULL,
                faceScore INTEGER,
                faceLabel TEXT,
                journalText TEXT NOT NULL,
                stressLevel INTEGER NOT NULL,
                themesString TEXT NOT NULL,
                suggestion TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Populate initial historical reflections
//        populateInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS mood_entries")
        onCreate(db)
    }

    private fun populateInitialData(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        val initialEntries = listOf(
            MoodEntry(
                id = "sample-1",
                timestamp = now - (dayMs * 0), // Today
                mood = MoodType.SAD,
                moodSource = "manual",
                faceScore = 68,
                faceLabel = "mostly sad",
                journalText = "Felt a bit overwhelmed with the project deadlines today. Tried to prioritize tasks but still feeling mentally drained.",
                stressLevel = 72,
                themes = listOf("Work pressure", "Mental fatigue", "Low motivation"),
                suggestion = "Take a 15-minute break and step outside if you can."
            ),
            MoodEntry(
                id = "sample-2",
                timestamp = now - (dayMs * 1), // Yesterday
                mood = MoodType.GOOD,
                moodSource = "face_scan",
                faceScore = 85,
                faceLabel = "mostly calm",
                journalText = "A quiet morning walk really set a positive tone for the day. Managed to finish reading a chapter of my favorite book.",
                stressLevel = 15,
                themes = listOf("Morning routine", "Peace of mind", "Focus"),
                suggestion = "Keep up the morning walk habit; it noticeably anchors your day."
            ),
            MoodEntry(
                id = "sample-3",
                timestamp = now - (dayMs * 2),
                mood = MoodType.NEUTRAL,
                moodSource = "manual",
                faceScore = 50,
                faceLabel = "thoughtful",
                journalText = "Normal day at work. Some meetings were long but handled them well. Enjoyed cooking dinner with relaxing music in the background.",
                stressLevel = 38,
                themes = listOf("Daily routine", "Evening unwind"),
                suggestion = "Notice how culinary focus helps you transition out of work mode."
            ),
            MoodEntry(
                id = "sample-4",
                timestamp = now - (dayMs * 3),
                mood = MoodType.JOYFUL,
                moodSource = "manual",
                faceScore = 92,
                faceLabel = "radiant smile",
                journalText = "Caught up with close friends after months! Laughed so much and shared good memories. Feeling very grateful for this circle.",
                stressLevel = 10,
                themes = listOf("Social connection", "Gratitude", "Laughter"),
                suggestion = "Cherish these uplifting moments and plan your next catch-up soon."
            ),
            MoodEntry(
                id = "sample-5",
                timestamp = now - (dayMs * 4),
                mood = MoodType.SAD,
                moodSource = "face_scan",
                faceScore = 60,
                faceLabel = "tired eyes",
                journalText = "Struggled to sleep last night, so energy was low throughout the afternoon. Had trouble concentrating on deep work.",
                stressLevel = 55,
                themes = listOf("Sleep deprivation", "Concentration fatigue"),
                suggestion = "Dim screens an hour earlier tonight and sip herbal chamomile tea."
            )
        )

        for (domain in initialEntries) {
            val entity = MoodEntryEntity.fromDomain(domain)
            db.execSQL(
                """
                INSERT OR REPLACE INTO mood_entries 
                (id, timestamp, moodIndex, moodSource, faceScore, faceLabel, journalText, stressLevel, themesString, suggestion)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf(
                    entity.id,
                    entity.timestamp,
                    entity.moodIndex,
                    entity.moodSource,
                    entity.faceScore,
                    entity.faceLabel,
                    entity.journalText,
                    entity.stressLevel,
                    entity.themesString,
                    entity.suggestion
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SerenityDatabase? = null

        fun getDatabase(context: Context): SerenityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = SerenityDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
