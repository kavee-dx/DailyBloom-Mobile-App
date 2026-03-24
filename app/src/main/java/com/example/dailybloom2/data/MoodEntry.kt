package com.example.dailybloom2.data

import java.util.*

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val note: String = "",
    val date: String, // Format: "2024-01-15"
    val time: String, // Format: "14:30"
    val timestamp: Long = System.currentTimeMillis()
)

data class MoodSummary(
    val date: String,
    val entries: List<MoodEntry>,
    val dominantMood: String, // Most frequent emoji
    val totalEntries: Int
)
