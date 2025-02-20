package com.example.ukk_armando

data class Task(
    val id: Int,              // ID tugas, akan di-set oleh database
    var description: String,  // Deskripsi tugas yang bisa diedit
    var date: String,         // Tanggal tugas
    var time: String,         // Waktu tugas
    var category: String,     // Kategori tugas (misalnya: Pekerjaan, Pribadi, dll)
    var isCompleted: Boolean, // Status apakah tugas sudah selesai
    var isHistory: Boolean    // Status apakah tugas sudah dipindahkan ke sejarah
) {
    // Constructor tambahan untuk membuat Task baru tanpa ID (default 0)
    constructor(description: String, date: String, time: String, category: String, isCompleted: Boolean, isHistory: Boolean) :
            this(0, description, date, time, category, isCompleted, isHistory) // ID default 0 sebelum disimpan di DB
}
