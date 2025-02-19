package com.example.ukk_armando

import android.os.Bundle
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

        // Inisialisasi adapter dengan memberikan context dan historyList
        historyAdapter = TaskAdapter(
            this,
            historyList,
            onTaskClicked = { task ->
                // Placeholder jika task diklik
            },
            onTaskDeleted = { task ->
                // Placeholder jika task dihapus (misalnya dari histori)
            },
            onTaskCompleted = { task ->
                // Placeholder jika task diselesaikan (tidak ada aksi karena sudah dipindahkan ke histori)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter

        // Memuat histori tugas
        loadHistory()
    }

    private fun loadHistory() {
        // Mengambil tugas yang sudah dipindahkan ke histori (isHistory = 1)
        historyList.clear()
        historyList.addAll(dbHelper.getHistoryTasks())
        historyAdapter.notifyDataSetChanged()
    }
}
