package com.example.ukk_armando

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "task_db"
        const val DATABASE_VERSION = 1
        const val TABLE_TASKS = "tasks"

        // Kolom dalam tabel tasks
        const val COL_ID = "id"
        const val COL_DESCRIPTION = "description"
        const val COL_DATE = "date"
        const val COL_TIME = "time"
        const val COL_CATEGORY = "category"
        const val COL_IS_COMPLETED = "isCompleted"
        const val COL_IS_HISTORY = "isHistory"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Membuat tabel tasks
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DESCRIPTION TEXT,
                $COL_DATE TEXT,
                $COL_TIME TEXT,
                $COL_CATEGORY TEXT,
                $COL_IS_COMPLETED INTEGER,
                $COL_IS_HISTORY INTEGER
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Menangani perubahan skema database (jika ada)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Menambahkan tugas baru ke dalam database
    fun addTask(task: Task) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DESCRIPTION, task.description)
            put(COL_DATE, task.date)
            put(COL_TIME, task.time)
            put(COL_CATEGORY, task.category)
            put(COL_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(COL_IS_HISTORY, if (task.isHistory) 1 else 0)
        }
        db.insert(TABLE_TASKS, null, values)
    }

    // Memperbarui tugas yang ada di database
    fun updateTask(task: Task) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DESCRIPTION, task.description)
            put(COL_DATE, task.date)
            put(COL_TIME, task.time)
            put(COL_CATEGORY, task.category)
            put(COL_IS_COMPLETED, if (task.isCompleted) 1 else 0)
            put(COL_IS_HISTORY, if (task.isHistory) 1 else 0)
        }
        db.update(TABLE_TASKS, values, "$COL_ID = ?", arrayOf(task.id.toString()))
    }

    // Memindahkan tugas ke histori (update isHistory menjadi 1)
    fun moveToHistory(taskId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_IS_HISTORY, 1)
        }
        db.update(TABLE_TASKS, values, "$COL_ID = ?", arrayOf(taskId.toString()))
    }

    // Mendapatkan semua tugas yang belum dipindahkan ke histori (isHistory = 0)
    @SuppressLint("Range")
    fun getActiveTasks(): List<Task> {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_TASKS,
            arrayOf(COL_ID, COL_DESCRIPTION, COL_DATE, COL_TIME, COL_CATEGORY, COL_IS_COMPLETED, COL_IS_HISTORY),
            "$COL_IS_HISTORY = ?",
            arrayOf("0"),  // Hanya tugas yang belum dipindahkan ke histori
            null, null, null
        )

        val taskList = mutableListOf<Task>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex(COL_ID))
                val description = it.getString(it.getColumnIndex(COL_DESCRIPTION))
                val date = it.getString(it.getColumnIndex(COL_DATE))
                val time = it.getString(it.getColumnIndex(COL_TIME))
                val category = it.getString(it.getColumnIndex(COL_CATEGORY))
                val isCompleted = it.getInt(it.getColumnIndex(COL_IS_COMPLETED)) == 1
                val isHistory = it.getInt(it.getColumnIndex(COL_IS_HISTORY)) == 1

                taskList.add(Task(id, description, date, time, category, isCompleted, isHistory))
            }
        }
        return taskList
    }

    // Mendapatkan semua tugas yang sudah dipindahkan ke histori (isHistory = 1)
    @SuppressLint("Range")
    fun getHistoryTasks(): List<Task> {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_TASKS,
            arrayOf(COL_ID, COL_DESCRIPTION, COL_DATE, COL_TIME, COL_CATEGORY, COL_IS_COMPLETED, COL_IS_HISTORY),
            "$COL_IS_HISTORY = ?",
            arrayOf("1"),  // Hanya tugas yang sudah dipindahkan ke histori
            null, null, null
        )

        val taskList = mutableListOf<Task>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex(COL_ID))
                val description = it.getString(it.getColumnIndex(COL_DESCRIPTION))
                val date = it.getString(it.getColumnIndex(COL_DATE))
                val time = it.getString(it.getColumnIndex(COL_TIME))
                val category = it.getString(it.getColumnIndex(COL_CATEGORY))
                val isCompleted = it.getInt(it.getColumnIndex(COL_IS_COMPLETED)) == 1
                val isHistory = it.getInt(it.getColumnIndex(COL_IS_HISTORY)) == 1

                taskList.add(Task(id, description, date, time, category, isCompleted, isHistory))
            }
        }
        return taskList
    }

    // Mengambil tugas berdasarkan ID
    @SuppressLint("Range")
    fun getTaskById(taskId: Int): Task {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_TASKS,
            arrayOf(COL_ID, COL_DESCRIPTION, COL_DATE, COL_TIME, COL_CATEGORY, COL_IS_COMPLETED, COL_IS_HISTORY),
            "$COL_ID = ?",
            arrayOf(taskId.toString()),
            null, null, null
        )

        cursor?.let {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                val description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION))
                val date = cursor.getString(cursor.getColumnIndex(COL_DATE))
                val time = cursor.getString(cursor.getColumnIndex(COL_TIME))
                val category = cursor.getString(cursor.getColumnIndex(COL_CATEGORY))
                val isCompleted = cursor.getInt(cursor.getColumnIndex(COL_IS_COMPLETED)) == 1
                val isHistory = cursor.getInt(cursor.getColumnIndex(COL_IS_HISTORY)) == 1

                return Task(id, description, date, time, category, isCompleted, isHistory)
            }
        }
        return Task(0, "", "", "", "", false, false)  // Mengembalikan task kosong jika tidak ditemukan
    }

    // Fungsi untuk menghapus task (jika diperlukan)
    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        db.delete(TABLE_TASKS, "$COL_ID = ?", arrayOf(taskId.toString()))
    }
}
