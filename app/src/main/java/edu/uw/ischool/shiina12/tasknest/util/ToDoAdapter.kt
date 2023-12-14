package edu.uw.ischool.shiina12.tasknest.util

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.shiina12.tasknest.R
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo



class TodoAdapter(
    private var items: List<Task>,
    private val onItemChecked: (Task, Int, ViewHolder) -> Unit, // Add a callback for when an item is checked
    private val onTaskDeleted: (Task) -> Unit,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onTaskTextClicked(currentTask: Task?)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.todoCheckBox)
        val textView: TextView = view.findViewById(R.id.todoTextView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.items_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = items[position]
        Log.i("ToDoAdapter", task.toString())
        holder.textView.text = task.title
        holder.checkBox.isChecked = task.isFinished

        // Set initial text appearance
        updateTextAppearance(holder.textView, task.isFinished, task)

        // Set a click listener for the checkbox
        holder.checkBox.setOnClickListener {
            onItemChecked(task, position, holder) // Call the passed in callback function
            updateTextAppearance(holder.textView, task.isFinished, task)
            holder.itemView.animate().alpha(0f).setDuration(300).withEndAction {
                // Remove the item after fade-out completes
                items = items.filter { it != task }
                notifyItemRemoved(position)
                onTaskDeleted(task) // Notify that a task has been deleted
            }
        }
        //Log.i("ToDoAdapter", holder.itemView.toString())

        holder.textView.setOnClickListener {
            val currentNest: TodoNest? = todoRepo.getTodoNestByTitle(todoRepo.getCurrNestName())
            val currentTask: Task? = currentNest?.tasks?.find {
                it.title == holder.textView.text
            }

            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                if (currentTask != null) {
                    onTaskTextClicked(currentTask)
                }
            }
        }

        if (task.isFinished) {
        }
    }

    private fun updateTextAppearance(textView: TextView, isFinished: Boolean, task: Task) {
        if (isFinished) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(Color.GRAY)
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary_text))
        }
        // Set background color based on colorHex
        try {
            // Set background color based on colorHex
            textView.setBackgroundColor(Color.parseColor(task.colorHex))
        } catch (e: IllegalArgumentException) {
            // Handle the case where colorHex is not a valid color
            e.printStackTrace()
            textView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount(): Int = items.size

    // Method to update the adapter's data
    fun updateItems(newItems: List<Task>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun onTaskTextClicked(currentTask: Task) {
        listener.onTaskTextClicked(currentTask)
    }
}




