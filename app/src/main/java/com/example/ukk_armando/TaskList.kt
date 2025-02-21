package com.example.ukk_armando

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private var taskList = ArrayList<Task>()
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var completeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.taskRecyclerView)
        completeButton = findViewById(R.id.completeButton)

        taskAdapter = TaskAdapter(
            this,
            taskList,
            onTaskClicked = { task ->
                // Placeholder jika task diklik
                Toast.makeText(this, "Task clicked: ${task.description}", Toast.LENGTH_SHORT).show()
            },
            onTaskDeleted = { task ->
                // Hapus tugas dari database dan list
                val rowsAffected = dbHelper.deleteTask(task.id)
                if (rowsAffected > 0) {
                    taskList.remove(task)
                    taskAdapter.notifyDataSetChanged()  // Update UI setelah penghapusan
                    Toast.makeText(this, "Tugas telah dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal menghapus tugas", Toast.LENGTH_SHORT).show()
                }
            },
            onTaskEdited = { task ->
                // Pindah ke Activity Edit untuk mengedit tugas
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("TASK_ID", task.id)  // Mengirim ID task yang akan diedit
                startActivity(intent)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        completeButton.setOnClickListener {
            val selectedTasks = taskAdapter.getSelectedTasks()
            selectedTasks.forEach { task ->
                val result = dbHelper.moveToHistory(task.id)
                if (result > 0) {
                    taskList.remove(task)
                } else {
                    Toast.makeText(this, "Gagal memindahkan tugas ${task.description}", Toast.LENGTH_SHORT).show()
                }
            }
            taskAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Tugas selesai dipindahkan ke histori", Toast.LENGTH_SHORT).show()
        }

        loadActiveTasks()
    }

    private fun loadActiveTasks() {
        taskList.clear()
        taskList.addAll(dbHelper.getActiveTasks())  // Mengambil tugas aktif
        taskAdapter.updateTaskList(taskList)  // Perbarui task list pada adapter
    }
}
