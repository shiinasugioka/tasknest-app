package edu.uw.ischool.shiina12.tasknest

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class HomeScreenNESTActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen_view_by_nest)

//      Identify Elements
        val nest_dropdown = findViewById<Spinner>(R.id.nest_drop_down)

//      Set Element values
        val nest_dropdown_items =
            arrayOf<String?>("Sample Nest 1", "Sample Nest 2", "Sample Nest 3")
        val arrayAdapter =
            ArrayAdapter<Any?>(this, R.layout.spinner_dropdown_text, nest_dropdown_items)
        nest_dropdown.adapter = arrayAdapter


    }
}