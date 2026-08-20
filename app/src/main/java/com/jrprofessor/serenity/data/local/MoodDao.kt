package com.jrprofessor.serenity.data.local

import kotlinx.coroutines.flow.Flow

interface MoodDao {
    fun getAllEntries(): Flow<List<MoodEntryEntity>>

    fun getEntriesSince(sinceTimestamp: Long): Flow<List<MoodEntryEntity>>

    suspend fun getEntryById(id: String): MoodEntryEntity?

    suspend fun getCount(): Int

    suspend fun insertEntry(entry: MoodEntryEntity)

    suspend fun insertEntries(entries: List<MoodEntryEntity>)

    suspend fun deleteEntry(entry: MoodEntryEntity)

    suspend fun clearAll()
}
