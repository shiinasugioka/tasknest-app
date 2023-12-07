package edu.uw.ischool.shiina12.tasknest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TodoAdapter(
    private var items: List<Task>,
    private val onItemChecked: (Task, Int) -> Unit // Add a callback for when an item is checked
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

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
        val item = items[position]
        holder.textView.text = item.title
        holder.checkBox.isChecked = item.isFinished

        // Set a click listener for the checkbox
        holder.checkBox.setOnClickListener {
            onItemChecked(item, position) // Call the passed in callback function
        }
    }

    override fun getItemCount(): Int = items.size

    // Method to update the adapter's data
    fun updateItems(newItems: List<Task>) {
        items = newItems
        notifyDataSetChanged()
    }
}


