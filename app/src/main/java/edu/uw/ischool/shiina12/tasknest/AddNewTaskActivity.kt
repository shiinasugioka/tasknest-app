package edu.uw.ischool.shiina12.tasknest

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddNewTaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var time: EditText
    private lateinit var date: EditText
    private lateinit var repeatingEvent: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_task)

        val timePickerFragment = TimePickerFragment()
        timePickerFragment.setListener(this)

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setListener(this)

        // https://developer.android.com/develop/ui/views/components/pickers
        time = findViewById(R.id.editTextTime)
        date = findViewById(R.id.editTextDate)
        repeatingEvent = findViewById(R.id.checkboxRepeating)

        time.setOnClickListener {
            timePickerFragment.show(supportFragmentManager, "timePicker")
        }
        date.setOnClickListener {
            datePickerFragment.show(supportFragmentManager, "datePicker")
        }
    }

    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        // fix time formatting
        var correctedHour = hourOfDay
        var isAm = true
        if (hourOfDay > 12) {
            correctedHour = hourOfDay - 12
            isAm = false
        }
        if (isAm) {
            time.setText("$correctedHour:$minute AM", TextView.BufferType.EDITABLE)

        } else {
            time.setText("$correctedHour:$minute PM", TextView.BufferType.EDITABLE)

        }
    }

    override fun onDateSet(year: Int, month: Int, day: Int) {
        // Do something with the date the user picks.
        val correctedMonth: Int = month + 1
        Log.i("TaskActivity", "in main $correctedMonth/$day/$year")
        date.setText("$correctedMonth/$day/$year", TextView.BufferType.EDITABLE)
    }
}
