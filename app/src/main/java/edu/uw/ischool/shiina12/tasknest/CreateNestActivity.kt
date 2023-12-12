package edu.uw.ischool.shiina12.tasknest

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo

class CreateNestActivity : AppCompatActivity() {

    private lateinit var createNestButton: Button
    private lateinit var nestTitleEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_nest)

        val exitBtn: ImageButton = findViewById(R.id.btn_exit)
        exitBtn.setOnClickListener { finish() }

        createNestButton = findViewById(R.id.btn_create_nest)
        nestTitleEdit = findViewById(R.id.edit_nest_title)

        val inflater = LayoutInflater.from(this)
        val nest = inflater.inflate(R.layout.homescreen_view_by_nest, null)
        val nestDropdown: Spinner = nest.findViewById(R.id.nest_drop_down)
        val nestDropdownItems = todoRepo.getAllNestTitles()

        val arrayAdapter =
            ArrayAdapter<Any?>(
                this@CreateNestActivity,
                R.layout.spinner_dropdown_text,
                nestDropdownItems
            )
        nestDropdown.adapter = arrayAdapter

        createNestButton.setOnClickListener {
            val nestName = nestTitleEdit.text.toString()
            val newNest = TodoNest(nestName, mutableListOf())
            todoRepo.todoNests.add(0, newNest)

            Toast.makeText(this, "New Nest '$nestName' created.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}