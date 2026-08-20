package com.jrprofessor.serenity

import com.jrprofessor.serenity.data.local.MoodDao
import com.jrprofessor.serenity.data.local.MoodEntryEntity
import com.jrprofessor.serenity.data.model.MoodEntry
import com.jrprofessor.serenity.data.model.MoodType
import com.jrprofessor.serenity.data.repository.MoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class MoodRepositoryStreakTest {

    private val fakeDao = object : MoodDao {
        override fun getAllEntries(): Flow<List<MoodEntryEntity>> = flowOf(emptyList())
        override fun getEntriesSince(sinceTimestamp: Long): Flow<List<MoodEntryEntity>> = flowOf(emptyList())
        override suspend fun getEntryById(id: String): MoodEntryEntity? = null
        override suspend fun getCount(): Int = 0
        override suspend fun insertEntry(entry: MoodEntryEntity) {}
        override suspend fun insertEntries(entries: List<MoodEntryEntity>) {}
        override suspend fun deleteEntry(entry: MoodEntryEntity) {}
        override suspend fun clearAll() {}
    }

    private val repository = MoodRepository(fakeDao)

    @Test
    fun calculateStreak_emptyList_returnsZero() {
        val streak = repository.calculateStreak(emptyList())
        assertEquals(0, streak)
    }

    @Test
    fun calculateStreak_consecutiveDays_returnsCorrectStreakCount() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        val entries = listOf(
            MoodEntry(
                timestamp = now,
                mood = MoodType.GOOD,
                journalText = "Today",
                stressLevel = 20,
                themes = emptyList(),
                suggestion = ""
            ),
            MoodEntry(
                timestamp = now - (dayMs * 1),
                mood = MoodType.SAD,
                journalText = "Yesterday",
                stressLevel = 60,
                themes = emptyList(),
                suggestion = ""
            ),
            MoodEntry(
                timestamp = now - (dayMs * 2),
                mood = MoodType.NEUTRAL,
                journalText = "2 days ago",
                stressLevel = 40,
                themes = emptyList(),
                suggestion = ""
            )
        )

        val streak = repository.calculateStreak(entries)
        assertEquals(3, streak)
    }
}
