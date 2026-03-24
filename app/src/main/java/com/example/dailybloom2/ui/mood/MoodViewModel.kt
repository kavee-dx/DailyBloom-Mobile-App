package com.example.dailybloom2.ui.mood

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailybloom2.data.MoodEntry
import com.example.dailybloom2.data.MoodRepository
import com.example.dailybloom2.data.MoodSummary
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "MoodViewModel"
    }

    private val repository = MoodRepository(application)

    private val _moodEntries = MutableLiveData<List<MoodEntry>>()
    val moodEntries: LiveData<List<MoodEntry>> = _moodEntries

    private val _todayMoodEntries = MutableLiveData<List<MoodEntry>>()
    val todayMoodEntries: LiveData<List<MoodEntry>> = _todayMoodEntries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        Log.d(TAG, "MoodViewModel initialized")
        loadMoodEntries()
    }

    fun loadMoodEntries() {
        Log.d(TAG, "Loading mood entries")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val allEntries = repository.getAllMoodEntries()
                val todayDate = getTodayDateString()
                val todayEntries = repository.getMoodEntriesForDate(todayDate)
                
                Log.d(TAG, "Today's date: $todayDate")
                Log.d(TAG, "Loaded ${allEntries.size} total entries, ${todayEntries.size} today")
                Log.d(TAG, "Today's entries: ${todayEntries.map { "${it.emoji} at ${it.time}" }}")
                
                _moodEntries.value = allEntries
                _todayMoodEntries.value = todayEntries
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error loading mood entries", e)
                _isLoading.value = false
            }
        }
    }
    
    fun addMoodEntry(emoji: String, note: String) {
        Log.d(TAG, "Adding mood entry: $emoji")
        viewModelScope.launch {
            try {
                val newEntry = repository.addMoodEntry(emoji, note)
                Log.d(TAG, "Mood entry added successfully: ${newEntry.id}")
                
                // Add a small delay to ensure the entry is saved
                kotlinx.coroutines.delay(100)
                
                loadMoodEntries()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding mood entry", e)
            }
        }
    }
    
    fun updateMoodEntry(entry: MoodEntry) {
        Log.d(TAG, "Updating mood entry: ${entry.id}")
        viewModelScope.launch {
            try {
                repository.updateMoodEntry(entry)
                loadMoodEntries()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating mood entry", e)
            }
        }
    }
    
    fun deleteMoodEntry(entryId: String) {
        Log.d(TAG, "Deleting mood entry: $entryId")
        viewModelScope.launch {
            try {
                repository.deleteMoodEntry(entryId)
                loadMoodEntries()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting mood entry", e)
            }
        }
    }
    
    fun getMoodEntriesForDate(date: String): List<MoodEntry> {
        return repository.getMoodEntriesForDate(date)
    }
    
    fun getMoodSummaryForDate(date: String): MoodSummary? {
        return repository.getMoodSummaryForDate(date)
    }
    
    fun getRecentMoodEntries(days: Int = 30): List<MoodEntry> {
        return repository.getRecentMoodEntries(days)
    }
    
    private fun getTodayDateString(): String {
        return repository.getTodayDateString()
    }
}
