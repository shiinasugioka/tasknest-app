package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeScreenButton: Button = findViewById(R.id.homescreen_button)

        homeScreenButton.setOnClickListener {
            goToHome()
        }
    }

    private fun goToHome() {
        val homeScreenIntent = Intent(this, HomeScreenActivity::class.java)
        startActivity(homeScreenIntent)
    }
}