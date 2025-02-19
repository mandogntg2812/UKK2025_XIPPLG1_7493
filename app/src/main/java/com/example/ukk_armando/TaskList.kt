package com.example.ukk_armando

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private var taskList = ArrayList<Task>()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        dbHelper = DatabaseHelper(this)

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.taskRecyclerView)

        // Inisialisasi adapter dengan memberikan context dan taskList
        taskAdapter = TaskAdapter(
            this,
            taskList,
            onTaskClicked = { task ->
                // Placeholder jika task diklik
            },
            onTaskDeleted = { task ->
                // Memindahkan task ke histori ketika dihapus
                dbHelper.moveToHistory(task.id)
                taskList.remove(task)
                taskAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Tugas dipindahkan ke histori", Toast.LENGTH_SHORT).show()
            },
            onTaskCompleted = { task ->
                // Memperbarui status tugas menjadi selesai dan memindahkan ke histori
                task.isCompleted = true
                dbHelper.updateTask(task)  // Update status tugas ke selesai
                dbHelper.moveToHistory(task.id)  // Memindahkan task ke histori
                taskList.remove(task)
                taskAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Tugas dipindahkan ke histori", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Memuat daftar tugas aktif
        loadActiveTasks()
    }

    private fun loadActiveTasks() {
        // Mengambil tugas yang belum dipindahkan ke histori (isHistory = 0)
        taskList.clear()
        taskList.addAll(dbHelper.getActiveTasks())  // Mengambil tugas aktif
        taskAdapter.notifyDataSetChanged()
    }
}
