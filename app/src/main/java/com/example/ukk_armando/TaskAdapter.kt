package com.example.ukk_armando

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private var taskList: ArrayList<Task>,  // taskList yang bisa diupdate
    private val onTaskClicked: (Task) -> Unit, // Callback untuk mengedit task
    private val onTaskDeleted: (Task) -> Unit, // Callback untuk menghapus task
    private val onTaskCompleted: (Task) -> Unit // Callback untuk menandai task selesai
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val dateTextView: TextView = itemView.findViewById(R.id.date)
        val timeTextView: TextView = itemView.findViewById(R.id.time)
        val categoryTextView: TextView = itemView.findViewById(R.id.category)
        val checkbox: CheckBox = itemView.findViewById(R.id.checkBox)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        init {
            // Menghandle klik pada item untuk mengedit task
            itemView.setOnClickListener {
                val task = taskList[adapterPosition]
                onTaskClicked(task)
            }

            // Menghandle klik pada tombol edit untuk mengedit task
            editButton.setOnClickListener {
                val task = taskList[adapterPosition]
                onTaskClicked(task)  // Memanggil callback untuk mengedit task
            }

            // Menghandle klik pada tombol delete untuk menghapus task
            deleteButton.setOnClickListener {
                val task = taskList[adapterPosition]
                onTaskDeleted(task)  // Memanggil callback untuk menghapus task
            }

            // Menghandle perubahan status checkbox (menandai task selesai)
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                val task = taskList[adapterPosition]
                task.isCompleted = isChecked
                onTaskCompleted(task) // Memanggil callback untuk menandai task selesai
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.descriptionTextView.text = task.description
        holder.dateTextView.text = task.date
        holder.timeTextView.text = task.time
        holder.categoryTextView.text = task.category

        // Menandai task yang sudah selesai dengan centang pada checkbox
        holder.checkbox.isChecked = task.isCompleted
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // Fungsi untuk memperbarui daftar task setelah ada perubahan
    fun updateTaskList(newTaskList: ArrayList<Task>) {
        taskList = newTaskList
        notifyDataSetChanged()
    }

    // Fungsi untuk menghapus task dari list
    fun deleteTask(task: Task) {
        taskList.remove(task)
        notifyDataSetChanged()
    }

    // Fungsi untuk memperbarui status task sebagai selesai
    fun updateTaskCompletion(task: Task) {
        // Menyimpan perubahan ke database (pastikan Anda sudah menambahkan metode updateTaskCompletion di DatabaseHelper)
        DatabaseHelper(context).updateTask(task)
        Toast.makeText(context, "Tugas selesai!", Toast.LENGTH_SHORT).show()
    }
}

