package edu.uw.ischool.shiina12.tasknest

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.shiina12.tasknest.util.NotificationScheduler
import java.util.concurrent.TimeUnit
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo

class HomeScreenNESTActivity : AppCompatActivity() {

    private lateinit var nest_dropdown: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_nest)

//      Identify Elements
        nest_dropdown = findViewById(R.id.nest_drop_down)
        val view_day_button: Button = findViewById(R.id.view_day_button)

        // Set Element values

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
        // Create channels for notifications
        createNotificationChannel(
            "app_channel",
            "App Notifications",
            "Channel for app notifications"
        )


        createNotificationChannel(
            "sms_channel",
            "SMS Notifications",
            "Channel for SMS notifications"
        )

        createNotificationChannel(
            "email_channel",
            "Email Notifications",
            "Channel for email notifications"
        )
    }

//    private fun testNotif() {
//
//        // Simulate an event time (e.g., current time + 10 seconds)
//        val eventTimeInMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2) + TimeUnit.SECONDS.toMillis(10)
//
//
//        // Immediately show a test notification
//        NotificationScheduler().scheduleNotification(this, eventTimeInMillis)
//    }

    override fun onStart() {
        super.onStart()
        setNewDropDownValues()
        setCurrentNest()
    }

    private fun setCurrentNest() {
        if (nest_dropdown.selectedItem != null) {
            val selectedNest = nest_dropdown.selectedItem.toString()
            todoRepo.setCurrNestName(selectedNest)
        }
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
                this,
                "Title '$newNestName' saved!",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
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
                    // depends on how the task list is populated. (start from beginning of task array)
                    // could reorganize into a new array
                    // todoRepo.todoNests.sortBy { it.dateCreated }
                    // sort tasks by date created

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
}