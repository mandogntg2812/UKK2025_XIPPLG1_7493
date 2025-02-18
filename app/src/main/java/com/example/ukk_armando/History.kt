package com.example.ukk_armando

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var tvHistory: TextView
    private val completedTaskList = mutableListOf<Task>() // This will hold completed tasks.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        tvHistory = findViewById(R.id.tvHistory)

        // Example data, this can be passed from MainActivity or shared ViewModel
        completedTaskList.add(Task("Task 1", "Monday", true))
        completedTaskList.add(Task("Task 2", "Tuesday", true))
        completedTaskList.add(Task("Task 3", "Wednesday", true))

        showCompletedTasks()
    }

    private fun showCompletedTasks() {
        if (completedTaskList.isEmpty()) {
            tvHistory.text = "No completed tasks yet."
        } else {
            val historyText = completedTaskList.joinToString("\n") { task ->
                "${task.description} on ${task.day}"
            }
            tvHistory.text = historyText
        }
    }
}