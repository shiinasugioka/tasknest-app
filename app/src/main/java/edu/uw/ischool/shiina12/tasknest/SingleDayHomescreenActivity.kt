package edu.uw.ischool.shiina12.tasknest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class SingleDayHomescreenActivity : AppCompatActivity() {

//    private lateinit var todoAdapter: TodoAdapter
//    private lateinit var repository: InMemoryTodoRepository
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_single_day_homescreen) // Your XML layout
//
//        // Accessing the InMemoryTodoRepository from the App class
//        repository = (application as App).todoRepository
//
//        // Initialize the RecyclerView
//        val recyclerView = findViewById<RecyclerView>(R.id.todoListRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Initialize your adapter with tasks for today
//        val tasksForToday = repository.getTasksForToday()
//        todoAdapter = TodoAdapter(tasksForToday) { task, position ->
//            // Logic for when an item's checkbox is checked
//            task.isFinished = true // Mark task as finished
//
//            //TODO CHANGE WHERE IT TAKES IN THE NEST
//            repository.getTodoNestByTitle("Personal")?.let { repository.deleteTask(it, task.title) } // Update the task in the repository
//            todoAdapter.updateItems(repository.getTasksForToday()) // Refresh the adapter
//        }
//
//        recyclerView.adapter = todoAdapter
//    }
}

