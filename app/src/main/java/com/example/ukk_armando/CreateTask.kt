package com.example.ukk_armando

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button

    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        descriptionEditText = findViewById(R.id.descriptionEditText)
        dateEditText = findViewById(R.id.dateEditText)
        timeEditText = findViewById(R.id.timeEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.saveButton)

        dbHelper = DatabaseHelper(this)

        calendar = Calendar.getInstance()

        // List kategori untuk spinner
        val categories = arrayOf("Pekerjaan", "Pribadi", "Olahraga", "Belajar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Dialog untuk memilih tanggal
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Dialog untuk memilih waktu
        timeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        // Menyimpan data task
        saveButton.setOnClickListener {
            val description = descriptionEditText.text.toString()
            val date = dateEditText.text.toString()
            val time = timeEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()

            // Validasi input
            if (description.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Membuat objek Task baru
            val newTask = Task(
                id = 0,
                description = description,
                date = date,
                time = time,
                category = category,  // Tidak perlu konversi ke Boolean
                isCompleted = false,
                isHistory = false
            )

            // Menyimpan task ke database
            dbHelper.insertTask(newTask)

            // Memberikan feedback
            Toast.makeText(this, "Tugas berhasil dibuat!", Toast.LENGTH_SHORT).show()
            finish()  // Menutup activity setelah task berhasil disimpan
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateEditText.setText(dateFormat.format(calendar.time))
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                timeEditText.setText(timeFormat.format(calendar.time))
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }
}
