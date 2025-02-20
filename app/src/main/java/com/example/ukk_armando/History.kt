package com.example.ukk_armando

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: TaskAdapter
    private var historyList = ArrayList<Task>()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dbHelper = DatabaseHelper(this)

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.historyRecyclerView)

        // Inisialisasi adapter dengan hanya menampilkan daftar histori (tanpa fitur edit, hapus, selesai)
        historyAdapter = TaskAdapter(
            this,
            historyList,
            onTaskClicked = { task ->
                // Tidak ada aksi karena tidak ada fitur edit di HistoryActivity
            },
            onTaskDeleted = { task ->
                onTaskDeleted(task) // Memanggil fungsi onTaskDeleted untuk menghapus task
            },
            onTaskEdited = { task ->
                // Tidak ada aksi edit di HistoryActivity
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        // Load data histori tugas dari database
        loadHistory()
    }

    private fun loadHistory() {
        historyList.clear()
        // Ambil histori dari database dan tambahkan ke list
        historyList.addAll(dbHelper.getHistoricalTasks())
        historyAdapter.notifyDataSetChanged()  // Update RecyclerView dengan data histori
    }

    private fun onTaskDeleted(task: Task) {
        // Menghapus task dari database
        val deletedRows = dbHelper.deleteTask(task.id)

        if (deletedRows > 0) {
            // Menghapus task dari list yang ditampilkan di RecyclerView
            historyList.remove(task)

            // Mengupdate RecyclerView untuk merefleksikan perubahan
            historyAdapter.notifyDataSetChanged()

            Toast.makeText(this, "Task berhasil dihapus", Toast.LENGTH_SHORT).show()
        }
    }
}
