package edu.uw.ischool.shiina12.tasknest

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.shiina12.tasknest.util.NotificationScheduler
import edu.uw.ischool.shiina12.tasknest.util.Task
import edu.uw.ischool.shiina12.tasknest.util.TodoAdapter
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import java.util.concurrent.TimeUnit
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo
import edu.uw.ischool.shiina12.tasknest.util.UtilFunctions as Functions

class HomeScreenNESTActivity : AppCompatActivity() {

    private lateinit var nestDropdown: Spinner
    private val nestHeaderMap = mutableMapOf<String, TextView>() // Maps deadline to headers
    private val nestRecyclerViewMap =
        mutableMapOf<String, RecyclerView>() // Maps deadline to RecyclerViews

    private lateinit var nest_dropdown: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_nest)

        nestDropdown = findViewById(R.id.nest_drop_down)

        setNewDropDownValues()

        val viewDayButton: Button = findViewById(R.id.view_day_button)

        val todoNestItemContainer: LinearLayout = findViewById(R.id.nest_layout_container)
        val spinnerSelectedNest: String? = if (nestDropdown.adapter.count > 0) {
            nestDropdown.selectedItem?.toString()
        } else {
            // Fallback behavior if the spinner is empty
            // You can return a default value or handle the situation accordingly
            "Default Nest"
        }

        setNewDropDownValues()

        nestDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                if (view != null) {
                    setCurrentNest()
                    val selectedNestName = parent.getItemAtPosition(position).toString()
                    loadTasksForSelectedNest(selectedNestName)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle the case when nothing is selected
            }
        }


        setNewDropDownValues()

        viewDayButton.setOnClickListener {
            switchToViewByDay()
        }

        // Settings Button Popup
        val settingsBtn: ImageButton = findViewById(R.id.settings_button)
        settingsBtn.setOnClickListener { showSettingsPopupMenu(it) }

        // New Nest Button
        val newNestBtn: Button = findViewById(R.id.new_nest_button)
        newNestBtn.setOnClickListener { createNest() }

        // New Task Button
        val newTaskBtn: ImageButton = findViewById(R.id.new_task_button)
        newTaskBtn.setOnClickListener { createNewTask() }

        // -- Notifications Code --
        // Create channels for notifications
        createNotificationChannel(
            "app_channel", "App Notifications", "Channel for app notifications"
        )

        createNotificationChannel(
            "sms_channel", "SMS Notifications", "Channel for SMS notifications"
        )
//
//        createNotificationChannel(
//            "email_channel",
//            "Email Notifications",
//            "Channel for email notifications"
//        )
    }

    private fun testNotif() {

        // Simulate an event time (e.g., current time + 10 seconds)
        val eventTimeInMillis =
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1) + TimeUnit.SECONDS.toMillis(10)


        // Immediately show a test notification
        NotificationScheduler().scheduleNotification(this, eventTimeInMillis, "Test")
    }

    override fun onStart() {
        super.onStart()
        setNewDropDownValues()
        setCurrentNest()
    }

    private fun setCurrentNest() {
        if (nestDropdown.selectedItem != null) {
            val selectedNest = nestDropdown.selectedItem.toString()
            todoRepo.setCurrNestName(selectedNest)

            Log.d(TAG, "in set current nest home screen, selectedNest: $selectedNest")
        }
    }

    private fun loadTasksForSelectedNest(nestName: String) {
        val todoNestItemContainer: LinearLayout = findViewById(R.id.nest_layout_container)
        todoNestItemContainer.removeAllViews()

        val selectedNest = todoRepo.getTodoNestByTitle(nestName)
        selectedNest?.let { nest ->
            val tasksGroupedByDeadline = nest.tasks.groupBy { it.apiDateTime }

            var hasTasks = false // Flag to check if there are any tasks

            tasksGroupedByDeadline.forEach { (deadline, tasks) ->
                val formattedDate =
                    Functions.reformatDate(deadline, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "EEEE, MM/dd")

                if (tasks.isNotEmpty()) {
                    // Create and add the deadline TextView
                    val deadlineTextView = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text = formattedDate
                        setTextColor(
                            ContextCompat.getColor(
                                context, R.color.primary_text
                            )
                        )
                        textSize = 13f
                    }
                    todoNestItemContainer.addView(deadlineTextView)

                    // Create and add the tasks view (RecyclerView or individual views)
                    val recyclerView =
                        createRecyclerViewForTasks(tasks, selectedNest, formattedDate)
                    todoNestItemContainer.addView(recyclerView)

                    hasTasks = true // Tasks are found
                }
            }

            if (!hasTasks) {
                // If no tasks were found, display a message
                val noTasksTextView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "No tasks for this nest."
                    setTextColor(
                        ContextCompat.getColor(
                            context, R.color.primary_text
                        )
                    )
                    textSize = 13f
                }
                todoNestItemContainer.addView(noTasksTextView)
            }
        }
    }



    private fun setNewDropDownValues() {
        val nestDropdownItems = todoRepo.getAllNestTitles()
        val arrayAdapter =
            ArrayAdapter<Any?>(this, R.layout.spinner_dropdown_text, nestDropdownItems)
        nestDropdown.adapter = arrayAdapter
        val receivedIntent = intent
        if (receivedIntent.hasExtra("currNest")) {
            val currNestName = receivedIntent.getStringExtra("currNest")
            nestDropdown.setSelection(arrayAdapter.getPosition(currNestName))
        }
        setCurrentNest()
    }

    private fun createNotificationChannel(channelId: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            this.description = description
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNest() {
        val createNestIntent = Intent(this, CreateNestActivity::class.java)
        startActivity(createNestIntent)
    }

    private fun createNewTask() {
        val createNewTaskIntent = Intent(this, AddNewTaskActivity::class.java)
        startActivity(createNewTaskIntent)
    }

    private fun showSettingsPopupMenu(view: View) {
        val contextWrapper = ContextThemeWrapper(this, R.style.PopupSettingsMenuStyle)
        val popupMenu = PopupMenu(contextWrapper, view)
        popupMenu.inflate(R.menu.settings_popup_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // Group 1: Nest Settings
                R.id.menu_delete_nest -> {
                    handleDeleteThisNest()
                    true
                }

                R.id.menu_rename_nest -> {
                    handleRenameThisNest()
                    true
                }

                // Group 2: App Settings
                R.id.menu_app_settings -> {
                    // Handle Settings or other actions in this group
                    val intent = Intent(this, PreferencesActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun handleDeleteThisNest() {
        val nestName = todoRepo.getCurrNestName()
        if (nestName.isNotBlank()) {
            todoRepo.removeNest(nestName)
            Toast.makeText(this, "Nest Removed.", Toast.LENGTH_SHORT).show()
        }
        setNewDropDownValues()
    }

    private fun handleRenameThisNest() {
        val nestName = todoRepo.getCurrNestName()
        if (nestName.isNotBlank()) {
            showRenamePopUp()
        }
    }

    private fun showRenamePopUp() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Rename your Nest.")

        val oldNestName = todoRepo.getCurrNestName()

        val input = EditText(this)
        input.hint = oldNestName
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val newNestName = input.text.toString()
            todoRepo.renameNest(oldNestName, newNestName)
            setNewDropDownValues()
            Toast.makeText(
                this, "Title '$newNestName' saved!", Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun switchToViewByDay() {
        val dayScreenIntent = Intent(this, HomeScreenDAYActivity::class.java)
        startActivity(dayScreenIntent)
        overridePendingTransition(0, 0)
    }

    private fun createRecyclerViewForTasks(
        tasks: List<Task>, todoNest: TodoNest, deadline: String
    ): RecyclerView {
        return RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@HomeScreenNESTActivity)
            adapter = TodoAdapter(tasks, { task, _, _ ->
                // Implement the logic to be executed when a task is checked
                task.isFinished = true
                todoRepo.modifyTask(todoNest, task.title, task)

                Handler(Looper.getMainLooper()).postDelayed({
                    todoRepo.deleteTask(todoNest, task.title)
                    val updatedTasks = todoRepo.getTasksFromNest(todoNest)
                    (this.adapter as? TodoAdapter)?.updateItems(updatedTasks)
                }, 300)
            }, {
                handleTaskDeleted(todoNest, deadline)
            }, object : TodoAdapter.OnItemClickListener {
                override fun onTaskTextClicked(currentTask: Task?) {
                    if (currentTask != null) {
                        onTaskTextClickedCalled(currentTask.title, currentTask.dateCreated)
                    }
                }
            })
        }
    }

    private fun handleTaskDeleted(todoNest: TodoNest, deadline: String) {
        if (todoNest.tasks.none { it.apiDateTime == deadline }) {
            // Remove header and RecyclerView for this deadline
            nestHeaderMap[deadline]?.let { headerView ->
                findViewById<LinearLayout>(R.id.nest_layout_container).removeView(headerView)
            }
            nestRecyclerViewMap[deadline]?.let { recyclerView ->
                findViewById<LinearLayout>(R.id.nest_layout_container).removeView(recyclerView)
            }
            nestHeaderMap.remove(deadline)
            nestRecyclerViewMap.remove(deadline)
        }
    }

    private fun onTaskTextClickedCalled(currentTaskName: String, dateCreated: String) {
        val viewTaskIntent = Intent(this, ViewTaskActivity::class.java)
        // add intents for task details
        Log.d(TAG, "from NEST: current task name: $currentTaskName, created on $dateCreated")
        viewTaskIntent.putExtra("currentTaskName", currentTaskName)
        viewTaskIntent.putExtra("dateCreated", dateCreated)

        Log.d(TAG, "from NEST: task text clicked!")
        startActivity(viewTaskIntent)
    }
}