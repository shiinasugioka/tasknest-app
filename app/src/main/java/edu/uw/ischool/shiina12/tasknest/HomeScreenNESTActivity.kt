package edu.uw.ischool.shiina12.tasknest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.ischool.shiina12.tasknest.util.Task
import edu.uw.ischool.shiina12.tasknest.util.TodoAdapter
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo

class HomeScreenNESTActivity : AppCompatActivity() {

    private lateinit var nest_dropdown: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_nest)

//      Identify Elements
        nest_dropdown = findViewById(R.id.nest_drop_down)

        setNewDropDownValues()

        val view_day_button: Button = findViewById(R.id.view_day_button)

//      Set Element values

        //Creating Tasks Loading

        val todoNestItemContainer: LinearLayout = findViewById(R.id.nest_layout_container)
        val spinnerSelectedNest: String? = if (nest_dropdown.adapter.count > 0) {
            nest_dropdown.selectedItem?.toString()
        } else {
            // Fallback behavior if the spinner is empty
            // You can return a default value or handle the situation accordingly
            "Default Nest"
        }

        val selectedNest = spinnerSelectedNest?.let { todoRepo.getTodoNestByTitle(it) }

        selectedNest?.let { nest ->
            val tasksGroupedByDeadline = nest.tasks.groupBy { it.deadline }

            tasksGroupedByDeadline.forEach { (deadline, tasks) ->
                if (tasks.isNotEmpty()) {
                    // Create and add the deadline TextView
                    val deadlineTextView = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        text = deadline?.let { formatDate(it) } // Set the formatted date text
                        setTextColor(ContextCompat.getColor(context, R.color.primary_text)) // Set text color
                        textSize = 13f // Set text size

                        // Additional styling if needed
                    }
                    todoNestItemContainer.addView(deadlineTextView)

                    // Create and add the tasks view (RecyclerView or individual views)
                    val recyclerView = createRecyclerViewForTasks(tasks,selectedNest)
                    todoNestItemContainer.addView(recyclerView)
                }
            }
        }






        setNewDropDownValues()

        view_day_button.setOnClickListener {
            switchToViewByDay()
        }

        // Settings Button Popup
        val settingsBtn: ImageButton = findViewById(R.id.settings_button)
        settingsBtn.setOnClickListener { showSettingsPopupMenu(it) }

        // Sort By Button Popup
        val sortButton: ImageButton = findViewById(R.id.sort_button)
        sortButton.setOnClickListener { showSortByPopupMenu(it) }

        // New Nest Button
        val newNestBtn: Button = findViewById(R.id.new_nest_button)
        newNestBtn.setOnClickListener { createNest() }

        // New Task Button
        val newTaskBtn: ImageButton = findViewById(R.id.new_task_button)
        newTaskBtn.setOnClickListener { createNewTask() }


        // -- Notifications Code --
        // Read preferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val appNotificationsEnabled = sharedPreferences.getBoolean("notification_app", true)
        val smsNotificationsEnabled = sharedPreferences.getBoolean("notification_sms", true)
        val emailNotificationsEnabled = sharedPreferences.getBoolean("notification_email", true)

        // Create notification channels based on preferences
        if (appNotificationsEnabled) {
            createNotificationChannel(
                "app_channel",
                "App Notifications",
                "Channel for app notifications"
            )
        }

        if (smsNotificationsEnabled) {
            createNotificationChannel(
                "sms_channel",
                "SMS Notifications",
                "Channel for SMS notifications"
            )
        }

        if (emailNotificationsEnabled) {
            createNotificationChannel(
                "email_channel",
                "Email Notifications",
                "Channel for email notifications"
            )
        }

    }

    override fun onStart() {
        super.onStart()
        setNewDropDownValues()
    }

    private fun setNewDropDownValues() {
        val nest_dropdown_items = todoRepo.getAllNestTitles()
        val arrayAdapter =
            ArrayAdapter<Any?>(this, R.layout.spinner_dropdown_text, nest_dropdown_items)
        nest_dropdown.adapter = arrayAdapter
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

        val titleItem1 = popupMenu.menu.findItem(R.id.app_settings_title)
        applyTitleUnderline(titleItem1)

        val titleItem2 = popupMenu.menu.findItem(R.id.nest_settings_title)
        applyTitleUnderline(titleItem2)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // Group 1: Nest Settings
                R.id.menu_delete_nest -> {
                    // TODO Handle Delete Nest or other actions in this group
                    true
                }

                R.id.menu_rename_nest -> {
                    // TODO Handle Rename Nest action
                    true
                }

                // Group 2: App Settings
                R.id.menu_app_settings -> {
                    // Handle Settings or other actions in this group
                    val intent = Intent(this, PreferencesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_google -> {
                    // TODO Handle Google action
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showSortByPopupMenu(view: View) {
        val contextWrapper = ContextThemeWrapper(this, R.style.PopupSortByMenuStyle)
        val popupMenu = PopupMenu(contextWrapper, view)
        popupMenu.inflate(R.menu.sort_by_menu)

        val titleItem = popupMenu.menu.findItem(R.id.sort_menu_title)
        applyTitleUnderline(titleItem)


        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_sort_date_created -> {
                    // TODO Handle sorting by date created
                    true
                }

                R.id.menu_sort_due_date -> {
                    // TODO Handle sorting by due date
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun applyTitleUnderline(titleItem: MenuItem) {
        val titleString = SpannableString(titleItem.title)
        titleString.setSpan(UnderlineSpan(), 0, titleString.length, 0)
        titleItem.title = titleString
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

    private fun createRecyclerViewForTasks(tasks: List<Task>, todoNest: TodoNest): RecyclerView {
        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@HomeScreenNESTActivity)
            adapter = TodoAdapter(tasks) { task, position, viewHolder ->
                // Implement the logic to be executed when a task is checked
                // For example: Update task, delete task, etc.
                task.isFinished = true // Mark task as finished
                todoRepo.modifyTask(
                    todoNest,
                    task.title,
                    task
                ) // Update the task in the todoRepo

                Handler(Looper.getMainLooper()).postDelayed({
                    todoRepo.deleteTask(todoNest, task.title)
                    val updatedTasks = todoRepo.getTasksFromNest(todoNest)
                    (this.adapter as? TodoAdapter)?.updateItems(updatedTasks)
                }, 300) // Delay to match the fade-out duration
            }
        }
        return recyclerView
    }

    fun formatDate(timestamp: Long): String {
        val deadlineDate = Date(timestamp)
        val currentDate = Calendar.getInstance().time

        // Pattern for date formatting
        val dateFormatPattern = "EEEE, MM/dd"
        val simpleDateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())

        val formattedDeadline = simpleDateFormat.format(deadlineDate)

        return if (SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(deadlineDate) == SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentDate)) {
            "Today, ${simpleDateFormat.format(deadlineDate)}"
        } else {
            formattedDeadline
        }
    }
}