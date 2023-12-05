package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nestButton: Button = findViewById(R.id.homescreen_NEST_button)
        val dayButton: Button = findViewById(R.id.homescreen_DAY_button)

        nestButton.setOnClickListener {
            goToNestHome()
        }

        dayButton.setOnClickListener {
            goToDayHome()
        }
    }

    private fun goToNestHome() {
        val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
        startActivity(nestScreenIntent)
    }

    private fun goToDayHome() {
        val dayScreenIntent = Intent(this, HomeScreenDAYActivity::class.java)
        startActivity(dayScreenIntent)
    }
}