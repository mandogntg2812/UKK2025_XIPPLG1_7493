package com.example.ukk_armando

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_IS_COMPLETED = "is_completed"
        private const val COLUMN_IS_HISTORY = "is_history"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TASKS_TABLE = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_IS_COMPLETED INTEGER,
                $COLUMN_IS_HISTORY INTEGER
            );
        """
        db?.execSQL(CREATE_TASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Insert task
    fun insertTask(task: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_DESCRIPTION, task.description)
        values.put(COLUMN_DATE, task.date)
        values.put(COLUMN_TIME, task.time)
        values.put(COLUMN_CATEGORY, task.category)
        values.put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        values.put(COLUMN_IS_HISTORY, if (task.isHistory) 1 else 0)

        return db.insert(TABLE_TASKS, null, values)
    }

    // Get active tasks (where is_history = 0)
    fun getActiveTasks(): ArrayList<Task> {
        val taskList = ArrayList<Task>()
        val db = this.readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_IS_HISTORY = 0"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val task = getTaskFromCursor(cursor)
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    // Get historical tasks (where is_history = 1)
    fun getHistoricalTasks(): ArrayList<Task> {
        val taskList = ArrayList<Task>()
        val db = this.readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_TASKS WHERE $COLUMN_IS_HISTORY = 1"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val task = getTaskFromCursor(cursor)
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    // Update task
    fun updateTask(task: Task): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_DESCRIPTION, task.description)
        values.put(COLUMN_DATE, task.date)
        values.put(COLUMN_TIME, task.time)
        values.put(COLUMN_CATEGORY, task.category)
        values.put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        values.put(COLUMN_IS_HISTORY, if (task.isHistory) 1 else 0)

        return db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
    }

    // Delete task
    fun deleteTask(taskId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }

    // Move task to history (set is_history = 1)
    fun moveToHistory(taskId: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_IS_HISTORY, 1)

        return db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }

    // Get a specific task by ID
    fun getTaskById(taskId: Int): Task? {
        if (taskId <= 0) {
            Log.e("DatabaseHelper", "Invalid task ID: $taskId")
            return null
        }

        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TASKS, arrayOf(
                COLUMN_ID, COLUMN_DESCRIPTION, COLUMN_DATE, COLUMN_TIME, COLUMN_CATEGORY,
                COLUMN_IS_COMPLETED, COLUMN_IS_HISTORY
            ),
            "$COLUMN_ID = ?", arrayOf(taskId.toString()), null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val task = getTaskFromCursor(cursor)
            cursor.close()
            db.close()
            return task
        }

        cursor?.close()
        db.close()
        return null
    }

    // Helper function to get Task from Cursor
    private fun getTaskFromCursor(cursor: Cursor): Task {
        // Mengambil indeks kolom
        val columnIndexId = cursor.getColumnIndex(COLUMN_ID)
        val columnIndexDescription = cursor.getColumnIndex(COLUMN_DESCRIPTION)
        val columnIndexDate = cursor.getColumnIndex(COLUMN_DATE)
        val columnIndexTime = cursor.getColumnIndex(COLUMN_TIME)
        val columnIndexCategory = cursor.getColumnIndex(COLUMN_CATEGORY)
        val columnIndexIsCompleted = cursor.getColumnIndex(COLUMN_IS_COMPLETED)
        val columnIndexIsHistory = cursor.getColumnIndex(COLUMN_IS_HISTORY)

        // Cek apakah kolom ditemukan di dalam cursor
        if (columnIndexId == -1 || columnIndexDescription == -1 || columnIndexDate == -1 ||
            columnIndexTime == -1 || columnIndexCategory == -1 || columnIndexIsCompleted == -1 ||
            columnIndexIsHistory == -1) {
            Log.e("DatabaseHelper", "Beberapa kolom tidak ditemukan dalam query result")
            throw Exception("Database column missing")
        }

        // Pastikan semua kolom ditemukan dan ambil data dari cursor
        return Task(
            id = cursor.getInt(columnIndexId),  // Ambil ID sebagai integer
            description = cursor.getString(columnIndexDescription),  // Ambil description sebagai String
            date = cursor.getString(columnIndexDate),  // Ambil date sebagai String
            time = cursor.getString(columnIndexTime),  // Ambil time sebagai String
            category = cursor.getString(columnIndexCategory),  // Ambil category sebagai String
            isCompleted = cursor.getInt(columnIndexIsCompleted) == 1,  // Status selesai sebagai boolean
            isHistory = cursor.getInt(columnIndexIsHistory) == 1  // Status sejarah sebagai boolean
        )
    }

}