package com.example.ukk_armando

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog

class TaskAdapter(
    private val context: Context,
    private val taskList: MutableList<Task>,
    private val onTaskCompleted: (Task) -> Unit,
    private val onEditClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int = taskList.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        private val taskDay: TextView = itemView.findViewById(R.id.tvTaskDay)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbTask)

        fun bind(task: Task) {
            taskDescription.text = task.description
            taskDay.text = task.day

            // Set checkbox status
            checkBox.isChecked = task.isCompleted
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Task completed
                    onTaskCompleted(task)
                }
            }

            // Handle item click for Edit/Delete
            itemView.setOnClickListener {
                showTaskOptions(task)
            }
        }

        private fun showTaskOptions(task: Task) {
            val options = arrayOf("Edit", "Delete")
            val builder = AlertDialog.Builder(context)
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> onEditClicked(task) // Edit option
                    1 -> confirmDelete(task) // Delete option
                }
            }
            builder.show()
        }

        private fun confirmDelete(task: Task) {
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes") { _, _ ->
                    onDeleteClicked(task)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
