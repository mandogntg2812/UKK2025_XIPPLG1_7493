package com.example.ukk_armando

data class Task(
    val id: Int,
    val description: String,
    val date: String,
    val time: String,
    val category: String,
    var isCompleted: Boolean,
    val isHistory: Boolean
)







