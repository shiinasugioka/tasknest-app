package edu.uw.ischool.shiina12.tasknest.util

import android.graphics.Color
import android.graphics.Paint
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
        holder.textView.text = task.title
        holder.checkBox.isChecked = task.isFinished

        // Set initial text appearance
        updateTextAppearance(holder.textView, task.isFinished)

        // Set a click listener for the checkbox
        holder.checkBox.setOnClickListener {
            onItemChecked(task, position, holder) // Call the passed in callback function
            updateTextAppearance(holder.textView, task.isFinished)
        }

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
            holder.itemView.animate().alpha(0f).setDuration(300).withEndAction {
                // Remove the item after fade-out completes
                items = items.filter { it != task }
                notifyItemRemoved(position)
            }
        } else {
            holder.itemView.alpha = 1f // Ensure full opacity if not finished
        }
    }

    private fun updateTextAppearance(textView: TextView, isFinished: Boolean) {
        if (isFinished) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(Color.GRAY)
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary_text))
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




