package com.jrprofessor.serenity.data.repository

import com.jrprofessor.serenity.data.local.MoodDao
import com.jrprofessor.serenity.data.local.MoodEntryEntity
import com.jrprofessor.serenity.data.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(private val moodDao: MoodDao) {

    val allEntries: Flow<List<MoodEntry>> = moodDao.getAllEntries().map { list ->
        list.map { it.toDomain() }
    }

    fun getEntriesForDays(days: Int): Flow<List<MoodEntry>> {
        val cutoff = System.currentTimeMillis() - (days.toLong() * 24 * 60 * 60 * 1000L)
        return moodDao.getEntriesSince(cutoff).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getEntryById(id: String): MoodEntry? {
        return moodDao.getEntryById(id)?.toDomain()
    }

    suspend fun saveEntry(entry: MoodEntry) {
        moodDao.insertEntry(MoodEntryEntity.fromDomain(entry))
    }

    suspend fun deleteEntry(entry: MoodEntry) {
        moodDao.deleteEntry(MoodEntryEntity.fromDomain(entry))
    }

    fun calculateStreak(entries: List<MoodEntry>): Int {
        if (entries.isEmpty()) return 0

        val cal = Calendar.getInstance()
        val sorted = entries.sortedByDescending { it.timestamp }

        // Set to beginning of today
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        // Set to beginning of yesterday
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val startOfYesterday = cal.timeInMillis

        val firstEntryTime = sorted.first().timestamp
        if (firstEntryTime < startOfYesterday) {
            // Streak broken if latest entry was before yesterday
            return 0
        }

        var streak = 0
        val checkedDays = mutableSetOf<String>()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)

        var expectedDateCal = Calendar.getInstance()
        if (firstEntryTime < startOfToday) {
            // Latest is yesterday
            expectedDateCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        for (entry in sorted) {
            val entryDateStr = sdf.format(Date(entry.timestamp))
            val expectedDateStr = sdf.format(expectedDateCal.time)

            if (entryDateStr == expectedDateStr) {
                if (checkedDays.add(entryDateStr)) {
                    streak++
                    expectedDateCal.add(Calendar.DAY_OF_YEAR, -1)
                }
            } else if (entryDateStr < expectedDateStr) {
                break
            }
        }

        return maxOf(streak, 1)
    }
}
