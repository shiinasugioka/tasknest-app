package edu.uw.ischool.shiina12.tasknest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

class CreateNestActivity : AppCompatActivity() {

    private val TAG = "CreateNestActivity"
    private lateinit var createNestButton: Button
    private lateinit var nestTitleEdit: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_nest)

        val exit_btn: ImageButton = findViewById(R.id.btn_exit)
        exit_btn.setOnClickListener { finish() }

        createNestButton = findViewById(R.id.btn_create_nest)
        nestTitleEdit = findViewById(R.id.edit_nest_title)

        createNestButton.setOnClickListener {
            var testing = nestTitleEdit.text
            Log.i(TAG, "$testing")
            var nestTitle = TodoNest(nestTitleEdit.text.toString(), mutableListOf())
            var todoRepo = InMemoryTodoRepository()
            todoRepo.todoNests.add(nestTitle)
        }
    }
}