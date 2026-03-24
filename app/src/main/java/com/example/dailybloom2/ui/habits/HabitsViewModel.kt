package com.example.dailybloom2.ui.habits

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailybloom2.data.Habit
import com.example.dailybloom2.data.HabitCompletionRecord
import com.example.dailybloom2.data.HabitRepository
import com.example.dailybloom2.data.HabitHistory
import com.example.dailybloom2.data.OverallHistory
import kotlinx.coroutines.launch

class HabitsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "HabitsViewModel"
    }

    private val repository = HabitRepository(application)

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> = _habits

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        Log.d(TAG, "HabitsViewModel initialized")
        loadHabits()
    }

    fun loadHabits() {
        Log.d(TAG, "Loading habits")
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.resetDailyProgress()
                val habitsList = repository.getAllHabits()
                Log.d(TAG, "Loaded ${habitsList.size} habits")
                _habits.value = habitsList
                _progress.value = repository.getTodayProgress()
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Error loading habits", e)
                _isLoading.value = false
            }
        }
    }
    
    fun addHabit(name: String) {
        Log.d(TAG, "Adding habit: $name")
        viewModelScope.launch {
            try {
                repository.addHabit(name)
                loadHabits()
            } catch (e: Exception) {
                Log.e(TAG, "Error adding habit", e)
            }
        }
    }
    
    fun editHabit(index: Int, newName: String) {
        Log.d(TAG, "Editing habit at index $index to: $newName")
        viewModelScope.launch {
            try {
                val currentHabits = _habits.value ?: return@launch
                if (index in currentHabits.indices) {
                    val habit = currentHabits[index].copy(name = newName)
                    repository.updateHabit(habit)
                    loadHabits()
                } else {
                    Log.w(TAG, "Index $index out of bounds")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error editing habit", e)
            }
        }
    }
    
    fun deleteHabit(index: Int) {
        Log.d(TAG, "Deleting habit at index: $index")
        viewModelScope.launch {
            try {
                val currentHabits = _habits.value ?: return@launch
                if (index in currentHabits.indices) {
                    val habit = currentHabits[index]
                    repository.deleteHabit(habit.id)
                    loadHabits()
                } else {
                    Log.w(TAG, "Index $index out of bounds")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting habit", e)
            }
        }
    }
    
    fun toggleCompleted(index: Int, isCompleted: Boolean) {
        Log.d(TAG, "Toggling habit at index $index to completed: $isCompleted")
        viewModelScope.launch {
            try {
                val currentHabits = _habits.value ?: return@launch
                if (index in currentHabits.indices) {
                    val habit = currentHabits[index]
                    val updatedHabit = habit.copy(
                        isCompleted = isCompleted,
                        completedAt = if (isCompleted) System.currentTimeMillis() else null
                    )
                    repository.updateHabit(updatedHabit)
                    loadHabits()
                } else {
                    Log.w(TAG, "Index $index out of bounds")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling habit", e)
            }
        }
    }
    
    fun refreshProgress() {
        _progress.value = repository.getTodayProgress()
    }
    
    fun getHabitHistory(habitId: String): HabitHistory? {
        return repository.getHabitHistory(habitId)
    }
    
    fun getOverallHistory(): OverallHistory {
        return repository.getOverallHistory()
    }
    
    fun getCompletionHistoryForDay(dateString: String): List<HabitCompletionRecord> {
        return repository.getCompletionHistoryForDay(dateString)
    }
}