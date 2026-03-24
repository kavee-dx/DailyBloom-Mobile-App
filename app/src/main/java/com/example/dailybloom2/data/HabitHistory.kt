package com.example.dailybloom2.data

import java.util.*

data class HabitCompletionRecord(
    val habitId: String,
    val habitName: String,
    val date: String, // Format: "2024-01-15"
    val isCompleted: Boolean,
    val completedAt: Long? = null
)

data class HabitHistory(
    val habitId: String,
    val habitName: String,
    val totalDays: Int,
    val completedDays: Int,
    val completionPercentage: Int,
    val recentRecords: List<HabitCompletionRecord>
)

data class OverallHistory(
    val totalHabits: Int,
    val totalDays: Int,
    val totalCompletedDays: Int,
    val overallPercentage: Int,
    val dailyHistory: List<DailyCompletionRecord>
)

data class DailyCompletionRecord(
    val date: String,
    val completedHabits: Int,
    val totalHabits: Int,
    val dailyPercentage: Int
)
