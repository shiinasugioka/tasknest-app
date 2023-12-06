package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class HomeScreenNESTActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_nest)

//      Identify Elements
        val nest_dropdown: Spinner = findViewById(R.id.nest_drop_down)
        val view_day_button: Button = findViewById(R.id.view_day_button)

//      Set Element values
        val nest_dropdown_items =
            arrayOf<String?>("Sample Nest 1", "Sample Nest 2", "Sample Nest 3")
        val arrayAdapter =
            ArrayAdapter<Any?>(this, R.layout.spinner_dropdown_text, nest_dropdown_items)
        nest_dropdown.adapter = arrayAdapter

        view_day_button.setOnClickListener {
            switchToViewByDay()
        }

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