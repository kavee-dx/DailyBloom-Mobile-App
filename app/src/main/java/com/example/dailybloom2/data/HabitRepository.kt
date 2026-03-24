package com.example.dailybloom2.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class HabitRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "HabitRepository"
        private const val KEY_HABITS = "habits_list"
        private const val KEY_DAILY_RESET = "daily_reset_date"
        private const val KEY_COMPLETION_HISTORY = "completion_history"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences("habits_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun getAllHabits(): List<Habit> {
        val habitsJson = prefs.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        val habits = gson.fromJson<List<Habit>>(habitsJson, type) ?: emptyList()
        Log.d(TAG, "Retrieved ${habits.size} habits from storage")
        return habits
    }
    
    fun saveHabits(habits: List<Habit>) {
        val habitsJson = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, habitsJson).apply()
        Log.d(TAG, "Saved ${habits.size} habits to storage")
    }
    
    fun addHabit(name: String): Habit {
        Log.d(TAG, "Adding habit: $name")
        val habits = getAllHabits().toMutableList()
        val newHabit = Habit(name = name)
        habits.add(newHabit)
        saveHabits(habits)
        Log.d(TAG, "Habit added successfully")
        return newHabit
    }
    
    fun updateHabit(habit: Habit) {
        Log.d(TAG, "Updating habit: ${habit.name}")
        val habits = getAllHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            val oldHabit = habits[index]
            habits[index] = habit
            saveHabits(habits)
            
            // Record completion change in history
            if (oldHabit.isCompleted != habit.isCompleted) {
                recordHabitCompletion(habit.id, habit.name, habit.isCompleted)
            }
            
            Log.d(TAG, "Habit updated successfully")
        } else {
            Log.w(TAG, "Habit not found for update")
        }
    }
    
    fun deleteHabit(habitId: String) {
        Log.d(TAG, "Deleting habit with ID: $habitId")
        val habits = getAllHabits().toMutableList()
        val initialSize = habits.size
        habits.removeAll { it.id == habitId }
        if (habits.size < initialSize) {
            saveHabits(habits)
            Log.d(TAG, "Habit deleted successfully")
        } else {
            Log.w(TAG, "Habit not found for deletion")
        }
    }
    
    fun toggleHabitCompletion(habitId: String): Habit? {
        val habits = getAllHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habitId }
        if (index != -1) {
            val habit = habits[index]
            val updatedHabit = habit.copy(
                isCompleted = !habit.isCompleted,
                completedAt = if (!habit.isCompleted) System.currentTimeMillis() else null
            )
            habits[index] = updatedHabit
            saveHabits(habits)
            
            // Record completion in history
            recordHabitCompletion(habitId, habit.name, updatedHabit.isCompleted)
            
            return updatedHabit
        }
        return null
    }
    
    fun getTodayProgress(): Int {
        val habits = getAllHabits()
        if (habits.isEmpty()) return 0
        
        val completedCount = habits.count { it.isCompleted }
        val progress = (completedCount * 100) / habits.size
        Log.d(TAG, "Progress: $completedCount/${habits.size} = $progress%")
        return progress
    }
    
    fun resetDailyProgress() {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val lastReset = prefs.getInt(KEY_DAILY_RESET, -1)
        
        if (lastReset != today) {
            Log.d(TAG, "Resetting daily progress for new day")
            val habits = getAllHabits().map { it.copy(isCompleted = false, completedAt = null) }
            saveHabits(habits)
            prefs.edit().putInt(KEY_DAILY_RESET, today).apply()
        }
    }
    
    // History tracking methods
    private fun getCompletionHistory(): MutableList<HabitCompletionRecord> {
        val historyJson = prefs.getString(KEY_COMPLETION_HISTORY, null) ?: return mutableListOf()
        val type = object : TypeToken<List<HabitCompletionRecord>>() {}.type
        return gson.fromJson<List<HabitCompletionRecord>>(historyJson, type)?.toMutableList() ?: mutableListOf()
    }
    
    private fun saveCompletionHistory(history: List<HabitCompletionRecord>) {
        val historyJson = gson.toJson(history)
        prefs.edit().putString(KEY_COMPLETION_HISTORY, historyJson).apply()
    }
    
    fun recordHabitCompletion(habitId: String, habitName: String, isCompleted: Boolean) {
        val today = getTodayDateString()
        val history = getCompletionHistory()
        
        // Remove existing record for today if any
        history.removeAll { it.habitId == habitId && it.date == today }
        
        // Add new record
        val record = HabitCompletionRecord(
            habitId = habitId,
            habitName = habitName,
            date = today,
            isCompleted = isCompleted,
            completedAt = if (isCompleted) System.currentTimeMillis() else null
        )
        history.add(record)
        
        // Keep only last 30 days of history
        val thirtyDaysAgo = getDateString(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) })
        history.removeAll { it.date < thirtyDaysAgo }
        
        saveCompletionHistory(history)
        Log.d(TAG, "Recorded completion for habit: $habitName, completed: $isCompleted")
    }
    
    fun getHabitHistory(habitId: String): HabitHistory? {
        val habits = getAllHabits()
        val habit = habits.find { it.id == habitId } ?: return null
        
        val history = getCompletionHistory()
        val habitRecords = history.filter { it.habitId == habitId }
        
        val totalDays = habitRecords.size
        val completedDays = habitRecords.count { it.isCompleted }
        val completionPercentage = if (totalDays > 0) (completedDays * 100) / totalDays else 0
        
        return HabitHistory(
            habitId = habitId,
            habitName = habit.name,
            totalDays = totalDays,
            completedDays = completedDays,
            completionPercentage = completionPercentage,
            recentRecords = habitRecords.takeLast(7) // Last 7 days
        )
    }
    
    fun getOverallHistory(): OverallHistory {
        val habits = getAllHabits()
        val history = getCompletionHistory()
        
        // Get unique dates from history
        val allDates = history.map { it.date }.distinct().sorted()
        
        val dailyRecords = allDates.map { date ->
            val dayRecords = history.filter { it.date == date }
            val completedCount = dayRecords.count { it.isCompleted }
            val totalCount = habits.size
            
            DailyCompletionRecord(
                date = date,
                completedHabits = completedCount,
                totalHabits = totalCount,
                dailyPercentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
            )
        }
        
        val totalDays = allDates.size
        val totalCompletedDays = dailyRecords.sumOf { it.completedHabits }
        val totalPossibleDays = habits.size * totalDays
        val overallPercentage = if (totalPossibleDays > 0) (totalCompletedDays * 100) / totalPossibleDays else 0
        
        return OverallHistory(
            totalHabits = habits.size,
            totalDays = totalDays,
            totalCompletedDays = totalCompletedDays,
            overallPercentage = overallPercentage,
            dailyHistory = dailyRecords.takeLast(7) // Last 7 days
        )
    }
    
    fun getCompletionHistoryForDay(dateString: String): List<HabitCompletionRecord> {
        val history = getCompletionHistory()
        return history.filter { it.date == dateString }
    }
    
    private fun getTodayDateString(): String {
        val calendar = Calendar.getInstance()
        return getDateString(calendar)
    }
    
    private fun getDateString(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }
}