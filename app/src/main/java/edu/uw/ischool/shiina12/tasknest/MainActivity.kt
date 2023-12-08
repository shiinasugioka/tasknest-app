package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nestButton: Button = findViewById(R.id.homescreen_button)
        val calButton: Button = findViewById(R.id.GoogleCalScreen_button)

        nestButton.setOnClickListener {
            goToNestHome()
        }

        calButton.setOnClickListener {
            goToTaskViewPage()
        }
    }

    private fun goToNestHome() {
        val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
        startActivity(nestScreenIntent)
    }

    private fun goToTaskViewPage() {
        val taskViewIntent = Intent(this, AddNewTaskActivity::class.java)
        startActivity(taskViewIntent)
    }
}