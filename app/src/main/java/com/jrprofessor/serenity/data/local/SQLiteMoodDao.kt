package com.jrprofessor.serenity.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SQLiteMoodDao(
    private val dbHelper: SQLiteOpenHelper
) : MoodDao {

    private val _entriesFlow = MutableStateFlow<List<MoodEntryEntity>>(emptyList())

    init {
        refreshFlow()
    }

    private fun refreshFlow() {
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                "mood_entries",
                null,
                null,
                null,
                null,
                null,
                "timestamp DESC"
            )
            val list = parseCursor(cursor)
            cursor.close()
            _entriesFlow.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getAllEntries(): Flow<List<MoodEntryEntity>> {
        return _entriesFlow.asStateFlow()
    }

    override fun getEntriesSince(sinceTimestamp: Long): Flow<List<MoodEntryEntity>> {
        return _entriesFlow.map { list ->
            list.filter { it.timestamp >= sinceTimestamp }.sortedBy { it.timestamp }
        }
    }

    override suspend fun getEntryById(id: String): MoodEntryEntity? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "mood_entries",
            null,
            "id = ?",
            arrayOf(id),
            null,
            null,
            null,
            "1"
        )
        val list = parseCursor(cursor)
        cursor.close()
        list.firstOrNull()
    }

    override suspend fun getCount(): Int = withContext(Dispatchers.IO) {
        _entriesFlow.value.size
    }

    override suspend fun insertEntry(entry: MoodEntryEntity) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", entry.id)
            put("timestamp", entry.timestamp)
            put("moodIndex", entry.moodIndex)
            put("moodSource", entry.moodSource)
            put("faceScore", entry.faceScore)
            put("faceLabel", entry.faceLabel)
            put("journalText", entry.journalText)
            put("stressLevel", entry.stressLevel)
            put("themesString", entry.themesString)
            put("suggestion", entry.suggestion)
        }
        db.insertWithOnConflict("mood_entries", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        refreshFlow()
    }

    override suspend fun insertEntries(entries: List<MoodEntryEntity>) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            for (entry in entries) {
                val values = ContentValues().apply {
                    put("id", entry.id)
                    put("timestamp", entry.timestamp)
                    put("moodIndex", entry.moodIndex)
                    put("moodSource", entry.moodSource)
                    put("faceScore", entry.faceScore)
                    put("faceLabel", entry.faceLabel)
                    put("journalText", entry.journalText)
                    put("stressLevel", entry.stressLevel)
                    put("themesString", entry.themesString)
                    put("suggestion", entry.suggestion)
                }
                db.insertWithOnConflict("mood_entries", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        refreshFlow()
    }

    override suspend fun deleteEntry(entry: MoodEntryEntity) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete("mood_entries", "id = ?", arrayOf(entry.id))
        refreshFlow()
    }

    override suspend fun clearAll() = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete("mood_entries", null, null)
        refreshFlow()
    }

    private fun parseCursor(cursor: Cursor): List<MoodEntryEntity> {
        val list = mutableListOf<MoodEntryEntity>()
        if (cursor.moveToFirst()) {
            val idIdx = cursor.getColumnIndexOrThrow("id")
            val tsIdx = cursor.getColumnIndexOrThrow("timestamp")
            val moodIdx = cursor.getColumnIndexOrThrow("moodIndex")
            val srcIdx = cursor.getColumnIndexOrThrow("moodSource")
            val faceScoreIdx = cursor.getColumnIndexOrThrow("faceScore")
            val faceLabelIdx = cursor.getColumnIndexOrThrow("faceLabel")
            val textIdx = cursor.getColumnIndexOrThrow("journalText")
            val stressIdx = cursor.getColumnIndexOrThrow("stressLevel")
            val themesIdx = cursor.getColumnIndexOrThrow("themesString")
            val suggIdx = cursor.getColumnIndexOrThrow("suggestion")

            do {
                val faceScore = if (cursor.isNull(faceScoreIdx)) null else cursor.getInt(faceScoreIdx)
                val faceLabel = if (cursor.isNull(faceLabelIdx)) null else cursor.getString(faceLabelIdx)

                list.add(
                    MoodEntryEntity(
                        id = cursor.getString(idIdx),
                        timestamp = cursor.getLong(tsIdx),
                        moodIndex = cursor.getInt(moodIdx),
                        moodSource = cursor.getString(srcIdx),
                        faceScore = faceScore,
                        faceLabel = faceLabel,
                        journalText = cursor.getString(textIdx),
                        stressLevel = cursor.getInt(stressIdx),
                        themesString = cursor.getString(themesIdx),
                        suggestion = cursor.getString(suggIdx)
                    )
                )
            } while (cursor.moveToNext())
        }
        return list
    }
}
