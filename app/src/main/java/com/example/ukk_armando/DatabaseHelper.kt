package com.example.ukk_armando

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "todolist.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DAY = "day"
        const val COLUMN_TIME = "time"
        const val COLUMN_IS_DONE = "is_done"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_TASKS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_DESCRIPTION TEXT, "
                + "$COLUMN_DAY TEXT, "
                + "$COLUMN_TIME TEXT, "
                + "$COLUMN_IS_DONE INTEGER DEFAULT 0)"
                )
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Menambahkan task baru
    fun addTask(description: String, day: String, time: String) {
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_DAY, day)
            put(COLUMN_TIME, time)
        }

        val db = this.writableDatabase
        db.insert(TABLE_TASKS, null, values)
        db.close()
    }

    // Mengambil semua task
    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_IS_DONE = 0", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    day = cursor.getString(cursor.getColumnIndex(COLUMN_DAY)),
                    time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME)),
                    isDone = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DONE)) == 1
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return taskList
    }

    // Mengambil task yang sudah selesai
    fun getCompletedTasks(): List<Task> {
        val completedTaskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_IS_DONE = 1", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    day = cursor.getString(cursor.getColumnIndex(COLUMN_DAY)),
                    time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME)),
                    isDone = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DONE)) == 1
                )
                completedTaskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return completedTaskList
    }

    // Memperbarui status task menjadi selesai
    fun markTaskAsDone(taskId: Int) {
        val values = ContentValues().apply {
            put(COLUMN_IS_DONE, 1)
        }

        val db = this.writableDatabase
        db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    // Menghapus task
    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }
}