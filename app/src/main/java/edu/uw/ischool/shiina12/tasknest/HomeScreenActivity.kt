package edu.uw.ischool.shiina12.tasknest

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class HomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homescreen)

//      Identify Elements
        val nest_dropdown = findViewById<Spinner>(R.id.nest_drop_down)

//      Set Element values
        val nest_dropdown_items =
            arrayOf<String?>("Sample Nest 1", "Sample Nest 2", "Sample Nest 3")
        val arrayAdapter =
            ArrayAdapter<Any?>(this, R.layout.spinner_dropdown_text, nest_dropdown_items)
        nest_dropdown.adapter = arrayAdapter

        // Popup settings
        val settings_button = findViewById<ImageButton>(R.id.settings_button)
        settings_button.setOnClickListener{
            showPopupMenu(it)
        }


    }

    private fun showPopupMenu(view: View) {
        val contextWrapper = ContextThemeWrapper(this, R.style.PopupMenuStyle)
        val popupMenu = PopupMenu(contextWrapper, view)
        popupMenu.inflate(R.menu.settings_popup_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // Group 1: Nest Settings
                R.id.group_nest_settings, R.id.menu_delete_nest -> {
                    // Handle Delete Nest or other actions in this group
                    true
                }
                R.id.menu_rename_nest -> {
                    // Handle Rename Nest action
                    true
                }

                // Group 2: App Settings
                R.id.group_app_settings, R.id.menu_settings -> {
                    // Handle Settings or other actions in this group
                    true
                }
                R.id.menu_google -> {
                    // Handle Google action
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }



}