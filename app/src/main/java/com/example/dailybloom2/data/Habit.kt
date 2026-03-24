package com.example.dailybloom2.data

import java.util.*

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
