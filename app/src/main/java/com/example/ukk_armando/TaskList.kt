package com.example.ukk_armando

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskActivity : AppCompatActivity() {

    private lateinit var taskRecyclerView: RecyclerView
    private val taskList = mutableListOf<Task>() // List of tasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        taskRecyclerView = findViewById(R.id.recyclerViewTasks)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        // Example data
        taskList.add(Task("Task 1", "Monday", false))
        taskList.add(Task("Task 2", "Tuesday", false))
        taskList.add(Task("Task 3", "Wednesday", false))

        val adapter = TaskAdapter(
            this,
            taskList,
            onTaskCompleted = { task -> taskCompleted(task) },
            onEditClicked = { task -> editTask(task) },
            onDeleteClicked = { task -> deleteTask(task) }
        )

        taskRecyclerView.adapter = adapter
    }

    private fun taskCompleted(task: Task) {
        // Mark the task as completed and move it to the HistoryActivity
        task.isCompleted = true
        Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show()

        // Move task to History (or remove from current list)
        taskList.remove(task)
        taskList.add(task)
        taskRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun editTask(task: Task) {
        // Implement editing task here
        Toast.makeText(this, "Editing task: ${task.description}", Toast.LENGTH_SHORT).show()
    }

    private fun deleteTask(task: Task) {
        // Remove the task from the list
        taskList.remove(task)
        taskRecyclerView.adapter?.notifyDataSetChanged()
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
    }
}
