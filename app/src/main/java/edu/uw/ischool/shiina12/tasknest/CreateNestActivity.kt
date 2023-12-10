package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import java.util.Arrays

class CreateNestActivity : AppCompatActivity() {

    private val TAG = "CreateNestActivity"
    private lateinit var createNestButton: Button
    private lateinit var nestTitleEdit: EditText
    private val todoRepo = InMemoryTodoRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_nest)

        val exit_btn: ImageButton = findViewById(R.id.btn_exit)
        exit_btn.setOnClickListener { finish() }

        createNestButton = findViewById(R.id.btn_create_nest)
        nestTitleEdit = findViewById(R.id.edit_nest_title)

        val inflater = LayoutInflater.from(this)
        val nest = inflater.inflate(R.layout.homescreen_view_by_nest, null)
        val nest_dropdown: Spinner = nest.findViewById(R.id.nest_drop_down)
        val nest_dropdown_items = todoRepo.getAllNestTitles()

        val arrayAdapter =
            ArrayAdapter<Any?>(this@CreateNestActivity, R.layout.spinner_dropdown_text, nest_dropdown_items)
        nest_dropdown.adapter = arrayAdapter

        createNestButton.setOnClickListener {
            val newNest = TodoNest(nestTitleEdit.text.toString(), mutableListOf())
            todoRepo.todoNests.add(newNest)
            val updatedNestDropdownItems = todoRepo.getAllNestTitles()
            val itemJustAdded = updatedNestDropdownItems[updatedNestDropdownItems.size - 1]
            Log.i(TAG, "just added $itemJustAdded")

            finish()
        }
    }
}