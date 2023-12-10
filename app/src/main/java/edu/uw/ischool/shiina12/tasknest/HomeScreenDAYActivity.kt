package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository
import edu.uw.ischool.shiina12.tasknest.util.TodoAdapter
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeScreenDAYActivity : AppCompatActivity() {

    private lateinit var repository: InMemoryTodoRepository
    private lateinit var nestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_day)

        repository = (application as App).todoRepository
        val linearLayoutContainer: LinearLayout = findViewById(R.id.linearLayoutContainer)
        nestButton = findViewById(R.id.view_nest_button)
        // Get today's date in millis to compare with task deadlines
        val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val todayBuilder = LocalDate.now()

        val formattedDate = todayBuilder.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

        // Set the formatted date to the TextView
        val dateTextView = findViewById<TextView>(R.id.day_title)
        dateTextView.text = formattedDate

        // Iterate through each TodoNest
        repository.createMultipleTodoLists().forEach { todoNest ->
            // Filter tasks for today
            val tasksForToday = todoNest.tasks.filter { it.deadline != null && it.deadline!! == today }

            // If there are tasks for today, show the nest title and tasks
            if (tasksForToday.isNotEmpty()) {
                // Create and add the title TextView
                val titleTextView = TextView(this).apply {
                    text = todoNest.title
                    textSize = 13f // Set text size
                    setTypeface(null, Typeface.BOLD) // Set text style to bold
                    typeface = ResourcesCompat.getFont(context, R.font.poppins) // Set font family
                    setTextColor(ContextCompat.getColor(context, R.color.primary_text)) // Set text color

                    val leftPaddingInPixels = (16 * resources.displayMetrics.density).toInt() // Example for 16dp
                    val topPaddingInPixels = (16 * resources.displayMetrics.density).toInt() // Example for 16dp
                    setPadding(leftPaddingInPixels, topPaddingInPixels, paddingRight, paddingBottom)
                }

                linearLayoutContainer.addView(titleTextView)

                // Create and add the RecyclerView for tasks
                val recyclerView = RecyclerView(this).apply {
                    layoutManager = LinearLayoutManager(this@HomeScreenDAYActivity)

                    val adapter = TodoAdapter(tasksForToday)  { task, _, viewHolder ->
                        task.isFinished = true // Mark task as finished
                        repository.modifyTask(todoNest, task.title, task) // Update the task in the repository

                        Handler(Looper.getMainLooper()).postDelayed({
                            repository.deleteTask(todoNest, task.title)
                            val updatedTasks = repository.getTasksForToday()
                            (this.adapter as? TodoAdapter)?.updateItems(updatedTasks)
                        }, 300) // Delay to match the fade-out duration
                    }
                    this.adapter = adapter
                }
                linearLayoutContainer.addView(recyclerView)
            }
        }
        nestButton.setOnClickListener {
            val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
            startActivity(nestScreenIntent)
        }
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
}