package edu.uw.ischool.shiina12.tasknest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class SingleDayHomescreenActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private val todoItems = mutableListOf<TodoItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_day_homescreen) // Your XML file name

        // Initialize the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.todoListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize your adapter with an empty list
        todoAdapter = TodoAdapter(todoItems)
        recyclerView.adapter = todoAdapter

        // Populate the list with example items
        todoItems.add(TodoItem("Complete Android Project"))
        todoItems.add(TodoItem("Call John"))
        // Add more items as needed

        todoAdapter.notifyDataSetChanged()
    }
}
