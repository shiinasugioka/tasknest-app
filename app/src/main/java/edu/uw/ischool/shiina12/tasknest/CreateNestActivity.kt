package edu.uw.ischool.shiina12.tasknest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton

class CreateNestActivity : AppCompatActivity() {

    private val TAG = "CreateNestActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_nest)

        val exit_btn: ImageButton = findViewById(R.id.btn_exit)
        exit_btn.setOnClickListener { finish() }
    }
}