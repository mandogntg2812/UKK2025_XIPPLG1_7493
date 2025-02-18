package com.example.ukk_armando

data class Task(
    val id: Int = 0,
    val description: String,
    val day: String,
    val time: String,
    val isDone: Boolean = false
)
