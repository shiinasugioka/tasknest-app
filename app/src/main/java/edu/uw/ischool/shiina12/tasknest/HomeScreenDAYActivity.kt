package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import edu.uw.ischool.shiina12.tasknest.util.Task
import edu.uw.ischool.shiina12.tasknest.util.TodoAdapter
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.view.View
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo

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
        // Get today's date in millis to compare with task deadlines
        val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val todayBuilder = LocalDate.now()

        val formattedDate = todayBuilder.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

        // Set the formatted date to the TextView
        val dateTextView = findViewById<TextView>(R.id.day_title)
        dateTextView.text = formattedDate



        // Iterate through each TodoNest
        todoRepo.createMultipleTodoLists().forEach { todoNest ->
            val tasksForToday = todoNest.tasks.filter { it.deadline != null && it.deadline!! == today }

            if (tasksForToday.isNotEmpty()) {
                // Create and add the title TextView
                val titleTextView = createTitleTextView(todoNest.title)
                linearLayoutContainer.addView(titleTextView)
                nestHeaderMap[todoNest] = titleTextView

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


    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun switchToViewByNest() {
        val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
        startActivity(nestScreenIntent)
        overridePendingTransition(0, 0)
    }

    private fun createTitleTextView(title: String): TextView {
        return TextView(this).apply {
            text = title
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.primary_text))
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

                linearLayoutContainer.requestLayout()

                Snackbar.make(findViewById(android.R.id.content), "The ${todoNest.title} nest is now empty!", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            nestRecyclerViewMap[todoNest]?.adapter?.let { adapter ->
                (adapter as TodoAdapter).updateItems(updatedTasks)
            }
        }
    }








}