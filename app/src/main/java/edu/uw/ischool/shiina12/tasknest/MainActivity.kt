package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, TaskActivity::class.java)
        startActivity(intent)
        Log.i(TAG, "MainActivity Launched")

        /*val nestButton: Button = findViewById(R.id.homescreen_button)

        nestButton.setOnClickListener {
            goToNestHome()
        }
    }

    private fun goToNestHome() {
        val nestScreenIntent = Intent(this, HomeScreenNESTActivity::class.java)
        startActivity(nestScreenIntent)
    }*/
    }
}