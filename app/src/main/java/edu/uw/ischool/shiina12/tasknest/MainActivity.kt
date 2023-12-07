package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homescreen)

        // Find the button by its ID
        val viewDayButton = findViewById<Button>(R.id.view_day_button)

        // Set a click listener on the button
        viewDayButton.setOnClickListener {

            val intent = Intent(this, SingleDayHomescreenActivity::class.java)

            // Start the activity
            startActivity(intent)
        }
    }
}