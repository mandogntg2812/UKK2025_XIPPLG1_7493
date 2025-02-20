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

        // Menyambungkan komponen UI dengan variabel
        recyclerView = findViewById(R.id.taskRecyclerView)
        completeButton = findViewById(R.id.completeButton)

        // Inisialisasi TaskAdapter
        taskAdapter = TaskAdapter(
            this,
            taskList,
            onTaskClicked = { task ->
                // Placeholder jika task diklik (bisa membuka activity edit)
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

        // Menangani tombol untuk memindahkan tugas selesai ke histori
        completeButton.setOnClickListener {
            val selectedTasks = taskAdapter.getSelectedTasks()

            // Pindahkan tugas yang dipilih ke histori
            selectedTasks.forEach { task ->
                val result = dbHelper.moveToHistory(task.id)
                if (result > 0) {
                    taskList.remove(task)  // Menghapus tugas dari daftar aktif
                } else {
                    Toast.makeText(this, "Gagal memindahkan tugas ${task.description}", Toast.LENGTH_SHORT).show()
                }
            }

            taskAdapter.notifyDataSetChanged()  // Update UI setelah pemindahan
            Toast.makeText(this, "Tugas selesai dipindahkan ke histori", Toast.LENGTH_SHORT).show()
        }

        // Memuat data tugas aktif dari database
        loadActiveTasks()
    }

    // Fungsi untuk memuat tugas aktif dari database
    private fun loadActiveTasks() {
        taskList.clear()
        taskList.addAll(dbHelper.getActiveTasks())  // Mengambil tugas aktif
        taskAdapter.updateTaskList(taskList)  // Perbarui task list pada adapter
    }
}
