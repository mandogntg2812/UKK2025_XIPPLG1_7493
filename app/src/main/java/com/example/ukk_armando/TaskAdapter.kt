package com.example.ukk_armando

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val context: Context,
    private var taskList: MutableList<Task>, // List yang akan ditampilkan
    private val onTaskClicked: (Task) -> Unit, // Callback untuk task clicked
    private val onTaskDeleted: (Task) -> Unit, // Callback untuk task deleted
    private val onTaskEdited: (Task) -> Unit  // Callback untuk task edited
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val selectedTasks = mutableSetOf<Task>()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val taskDescription: TextView = itemView.findViewById(R.id.description)

        init {
            // Listener untuk checkbox
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                val task = taskList[adapterPosition]
                if (isChecked) {
                    selectedTasks.add(task) // Tambahkan task ke dalam selectedTasks
                } else {
                    selectedTasks.remove(task) // Hapus task dari selectedTasks
                }
            }

            // Menangani klik editButton
            editButton.setOnClickListener {
                val task = taskList[adapterPosition]
                onTaskEdited(task)  // Mengirim task untuk diedit
            }

            // Menangani klik deleteButton
            deleteButton.setOnClickListener {
                val task = taskList[adapterPosition]
                onTaskDeleted(task)  // Menghapus task
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Menampilkan deskripsi tugas
        holder.taskDescription.text = task.description

        // Menangani status checkbox berdasarkan selectedTasks
        holder.taskCheckbox.isChecked = selectedTasks.contains(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // Mengambil tugas yang terpilih
    fun getSelectedTasks(): Set<Task> {
        return selectedTasks
    }

    // Memperbarui task list
    fun updateTaskList(newTaskList: MutableList<Task>) {
        taskList = newTaskList
        notifyDataSetChanged() // Memberi tahu adapter jika ada perubahan
    }
}
