package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.uw.ischool.shiina12.tasknest.util.Task
import edu.uw.ischool.shiina12.tasknest.util.TodoAdapter
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo
import edu.uw.ischool.shiina12.tasknest.util.UtilFunctions as Functions

class HomeScreenDAYActivity : AppCompatActivity() {

    private lateinit var nestButton: Button
    private val nestHeaderMap = mutableMapOf<TodoNest, TextView>()
    private val nestRecyclerViewMap = mutableMapOf<TodoNest, RecyclerView>()
    private lateinit var linearLayoutContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_day)

        val linearLayoutContainer: LinearLayout = findViewById(R.id.linearLayoutContainer)

        // New Task Button
        val newTaskBtn: ImageButton = findViewById(R.id.new_task_button)
        newTaskBtn.setOnClickListener { createNewTask() }

        nestButton = findViewById(R.id.view_nest_button)
        // Settings Button Popup
        val settingsBtn: ImageButton = findViewById(R.id.settings_button)
        settingsBtn.setOnClickListener { showSettingsPopupMenu(it) }

        // Get today's date in ISO to compare with task deadlines
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val today = dateFormat.format(Date())
        val formattedDate =
            Functions.reformatDate(today, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "MMMM d, yyyy")

        // Set the formatted date to the TextView
        val dateTextView = findViewById<TextView>(R.id.day_title)
        dateTextView.text = formattedDate

        // Iterate through each TodoNest
        val todayFormatted =
            Functions.reformatDate(today, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd")

        // Iterate through each TodoNest
        todoRepo.createMultipleTodoLists().forEach { todoNest ->
            // Filter tasks for today using simplified date format
            val tasksForToday = todoNest.tasks.filter {
                val taskDateFormatted = Functions.reformatDate(
                    it.apiDateTime, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd"
                )
                taskDateFormatted == todayFormatted
            }

            // If there are tasks for today, show the nest title and tasks
            if (tasksForToday.isNotEmpty()) {
                // Create and add the title TextView
                val titleTextView = createTitleTextView(todoNest.title)
                linearLayoutContainer.addView(titleTextView)
                nestHeaderMap[todoNest] = titleTextView

//<<<<<<< HEAD
//                // Create and add the RecyclerView for tasks
//                val recyclerView = RecyclerView(this).apply {
//                    layoutManager = LinearLayoutManager(this@HomeScreenDAYActivity)
//
//                    val adapter = TodoAdapter(tasksForToday, { task, _, _ ->
//                        task.isFinished = true // Mark task as finished
//                        todoRepo.modifyTask(
//                            todoNest, task.title, task
//                        ) // Update the task in the todoRepo
//
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            todoRepo.deleteTask(todoNest, task.title)
//                            val updatedTasks = todoRepo.getTasksForToday()
//                            (this.adapter as? TodoAdapter)?.updateItems(updatedTasks)
//                        }, 300) // Delay to match the fade-out duration
//                    }, object : TodoAdapter.OnItemClickListener {
//                        override fun onTaskTextClicked(currentTask: Task?) {
//                            if (currentTask != null) {
//                                onTaskTextClickedCalled(currentTask.title, currentTask.dateCreated)
//                            }
//                        }
//                    })
//                    this.adapter = adapter
//                }
//=======
//>>>>>>> main

                // Create and add the RecyclerView for tasks
                val recyclerView = createRecyclerViewForTasks(todoNest, tasksForToday)
                linearLayoutContainer.addView(recyclerView)
                nestRecyclerViewMap[todoNest] = recyclerView
            }
        }


        nestButton.setOnClickListener {
            val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
            startActivity(nestScreenIntent)
        }
    }

    private fun createNewTask() {
        val createNewTaskIntent = Intent(this, AddNewTaskActivity::class.java)
        startActivity(createNewTaskIntent)
    }

    private fun showSettingsPopupMenu(view: View) {
        val contextWrapper = ContextThemeWrapper(this, R.style.PopupSettingsMenuStyle)
        val popupMenu = PopupMenu(contextWrapper, view)
        popupMenu.inflate(R.menu.settings_popup_menu)

        // Identify items to remove (excluding menu_app_settings)
        val itemsToRemove = mutableListOf<Int>()
        for (i in 0 until popupMenu.menu.size()) {
            val itemId = popupMenu.menu.getItem(i).itemId
            if (itemId != R.id.menu_app_settings) {
                itemsToRemove.add(itemId)
            }
        }

        // Remove identified items
        for (itemId in itemsToRemove) {
            popupMenu.menu.removeItem(itemId)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_app_settings -> {
                    val intent = Intent(this, PreferencesActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun switchToViewByNest() {
        val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
        startActivity(nestScreenIntent)
        overridePendingTransition(0, 0)
    }

    private fun onTaskTextClickedCalled(currentTaskName: String, dateCreated: String) {
        val viewTaskIntent = Intent(this, ViewTaskActivity::class.java)
        // add intents for task details
        Log.d(TAG, "from DAY: current task name: $currentTaskName, created on $dateCreated")
        viewTaskIntent.putExtra("currentTaskName", currentTaskName)
        viewTaskIntent.putExtra("dateCreated", dateCreated)

        Log.d(TAG, "from DAY: task text clicked!")
        startActivity(viewTaskIntent)
    }

    private fun createTitleTextView(title: String): TextView {
        return TextView(this).apply {
            text = title
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(
                ContextCompat.getColor(
                    context, R.color.primary_text
                )
            ) // Set text color
            val leftPaddingInPixels = (16 * resources.displayMetrics.density).toInt()
            val topPaddingInPixels = (1 * resources.displayMetrics.density).toInt()
            setPadding(leftPaddingInPixels, topPaddingInPixels, paddingRight, paddingBottom)
        }
    }

    private fun createRecyclerViewForTasks(todoNest: TodoNest, tasks: List<Task>): RecyclerView {
        return RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@HomeScreenDAYActivity)
            adapter = TodoAdapter(tasks, { task, _, _ ->
                handleTaskChecked(todoNest, task)
            }, { deletedTask ->
                handleTaskDeleted(todoNest, deletedTask.title)
            }, object : TodoAdapter.OnItemClickListener {
                override fun onTaskTextClicked(currentTask: Task?) {
                    if (currentTask != null) {
                        onTaskTextClickedCalled(currentTask.title, currentTask.dateCreated)
                    }
                }
            })
        }
    }

    private fun handleTaskChecked(todoNest: TodoNest, task: Task) {
        task.isFinished = true
        todoRepo.modifyTask(todoNest, task.title, task)
        Handler(Looper.getMainLooper()).postDelayed({
            todoRepo.deleteTask(todoNest, task.title)
            val updatedTasks = todoRepo.getTasksForToday()
            nestRecyclerViewMap[todoNest]?.adapter?.let { adapter ->
                (adapter as TodoAdapter).updateItems(updatedTasks)
            }
        }, 300)
    }

    private fun handleTaskDeleted(todoNest: TodoNest, deletedTaskTitle: String) {
        todoRepo.deleteTask(todoNest, deletedTaskTitle)
        val updatedTasks = todoRepo.getTasksForToday()

        if (todoNest.tasks.isEmpty()) {
            runOnUiThread {
                nestHeaderMap[todoNest]?.let { headerView ->
                    linearLayoutContainer.removeView(headerView)
                }
                nestRecyclerViewMap[todoNest]?.let { recyclerView ->
                    linearLayoutContainer.removeView(recyclerView)
                }
                nestHeaderMap.remove(todoNest)
                nestRecyclerViewMap.remove(todoNest)

                Snackbar.make(
                    findViewById(android.R.id.content),
                    "The ${todoNest.title} nest is now empty!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            nestRecyclerViewMap[todoNest]?.adapter?.let { adapter ->
                (adapter as TodoAdapter).updateItems(updatedTasks)
            }
        }
    }
}