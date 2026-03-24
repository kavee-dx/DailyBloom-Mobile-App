package com.example.dailybloom2.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MoodRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "MoodRepository"
        private const val KEY_MOOD_ENTRIES = "mood_entries"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences("mood_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun getAllMoodEntries(): List<MoodEntry> {
        val entriesJson = prefs.getString(KEY_MOOD_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        val entries = gson.fromJson<List<MoodEntry>>(entriesJson, type) ?: emptyList()
        Log.d(TAG, "Retrieved ${entries.size} mood entries from storage")
        return entries
    }
    
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val entriesJson = gson.toJson(entries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, entriesJson).apply()
        Log.d(TAG, "Saved ${entries.size} mood entries to storage")
    }
    
    fun addMoodEntry(emoji: String, note: String): MoodEntry {
        Log.d(TAG, "Adding mood entry: $emoji")
        val entries = getAllMoodEntries().toMutableList()
        val newEntry = MoodEntry(
            emoji = emoji,
            note = note,
            date = getTodayDateString(),
            time = getCurrentTimeString()
        )
        entries.add(newEntry)
        saveMoodEntries(entries)
        Log.d(TAG, "Mood entry added successfully")
        return newEntry
    }
    
    fun updateMoodEntry(entry: MoodEntry) {
        Log.d(TAG, "Updating mood entry: ${entry.id}")
        val entries = getAllMoodEntries().toMutableList()
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry
            saveMoodEntries(entries)
            Log.d(TAG, "Mood entry updated successfully")
        } else {
            Log.w(TAG, "Mood entry not found for update")
        }
    }
    
    fun deleteMoodEntry(entryId: String) {
        Log.d(TAG, "Deleting mood entry with ID: $entryId")
        val entries = getAllMoodEntries().toMutableList()
        val initialSize = entries.size
        entries.removeAll { it.id == entryId }
        if (entries.size < initialSize) {
            saveMoodEntries(entries)
            Log.d(TAG, "Mood entry deleted successfully")
        } else {
            Log.w(TAG, "Mood entry not found for deletion")
        }
    }
    
    fun getMoodEntriesForDate(date: String): List<MoodEntry> {
        val allEntries = getAllMoodEntries()
        return allEntries.filter { it.date == date }
    }
    
    fun getMoodEntriesForDateRange(startDate: String, endDate: String): List<MoodEntry> {
        val allEntries = getAllMoodEntries()
        return allEntries.filter { it.date >= startDate && it.date <= endDate }
    }
    
    fun getMoodSummaryForDate(date: String): MoodSummary? {
        val entries = getMoodEntriesForDate(date)
        if (entries.isEmpty()) return null
        
        // Find most frequent emoji
        val emojiCount = entries.groupingBy { it.emoji }.eachCount()
        val dominantMood = emojiCount.maxByOrNull { it.value }?.key ?: entries.first().emoji
        
        return MoodSummary(
            date = date,
            entries = entries,
            dominantMood = dominantMood,
            totalEntries = entries.size
        )
    }
    
    fun getRecentMoodEntries(days: Int = 30): List<MoodEntry> {
        val allEntries = getAllMoodEntries()
        val cutoffDate = getDateString(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -days) })
        return allEntries.filter { it.date >= cutoffDate }
    }
    
    fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        return getDateString(calendar)
    }
    
    private fun getCurrentTimeString(): String {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
    
    private fun getDateString(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
}
