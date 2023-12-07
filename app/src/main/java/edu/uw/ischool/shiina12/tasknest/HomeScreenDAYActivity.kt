package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeScreenDAYActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_day)

//      Identify Elements
        val view_nest_button: Button = findViewById(R.id.view_nest_button)

//      Set Element values


        view_nest_button.setOnClickListener {
            switchToViewByNest()
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