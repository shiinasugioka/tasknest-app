package edu.uw.ischool.shiina12.tasknest

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView


class TaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var time: EditText
    private lateinit var date: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var repeatingEvent: CheckBox
    private lateinit var repeatingEventLayout: LinearLayout
    private lateinit var startsOn: EditText
    private lateinit var endsOn: EditText
    private lateinit var atTime: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // https://developer.android.com/develop/ui/views/components/pickers
        time = findViewById(R.id.editTextTime)
        date = findViewById(R.id.editTextDate)
        repeatingEvent = findViewById(R.id.checkboxRepeating)

        val inflater = LayoutInflater.from(this)
        val dialogLayout = inflater.inflate(R.layout.repeating_dialog_layout, null)

        // repeatingEventLayout = dialogLayout.findViewById(R.id.repeatingEventOptions)
        startsOn = dialogLayout.findViewById(R.id.startsOn)
        endsOn = dialogLayout.findViewById(R.id.endsOn)
        atTime = dialogLayout.findViewById(R.id.reminderTime)

        val timePickerFragment = TimePickerFragment()
        timePickerFragment.setListener(this, time)

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setListener(this, date)

        val startDateFragment = DatePickerFragment()
        startDateFragment.setListener(this, startsOn)

        val endDateFragment = DatePickerFragment()
        endDateFragment.setListener(this, endsOn)

        val atTimeFragment = TimePickerFragment()
        atTimeFragment.setListener(this, atTime)


        time.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                timePickerFragment.show(supportFragmentManager, "timePicker")
            }
        }
        date.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                datePickerFragment.show(supportFragmentManager, "datePicker")
            }
        }

        startsOn.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                startDateFragment.show(supportFragmentManager, "datePicker")
            }
        }

        endsOn.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                endDateFragment.show(supportFragmentManager, "datePicker")
            }
        }

        atTime.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                atTimeFragment.setListener(this, atTime)
                atTimeFragment.show(supportFragmentManager, "timePicker")
            }
        }

        checkBox = findViewById(R.id.checkboxRepeating)
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                showCustomDialog()
            }
        }
    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.repeating_dialog_layout, null)

        //val editText = dialogView.findViewById<EditText>(R.id.editText)

        builder.setView(dialogView)
            .setTitle("Options")
            .setPositiveButton("OK") { dialog, _ ->
                // val enteredText = editText.text.toString()
                // Do something with the entered text
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    override fun onTimeSet(hourOfDay: Int, minute: Int, targetEditText: EditText?) {
        var correctedHour = hourOfDay
        var isAm = true
        if (hourOfDay > 12) {
            Log.i("AddNewTaskActivity", hourOfDay.toString())
            correctedHour = hourOfDay - 12
            isAm = false
        } else if (hourOfDay == 0) {
            correctedHour = 12
        }

        var formattedTime: String = if (minute < 10) {
            "$correctedHour:0$minute"
        } else {
            "$correctedHour:$minute"
        }

        formattedTime += if (isAm) " AM" else " PM"
        targetEditText?.setText(formattedTime, TextView.BufferType.EDITABLE)
    }

    override fun onDateSet(year: Int, month: Int, day: Int, targetEditText: EditText?) {
        // Do something with the date the user picks.
        val correctedMonth: Int = month + 1
        Log.i("TaskActivity", "in main $correctedMonth/$day/$year")
        targetEditText?.setText("$correctedMonth/$day/$year", TextView.BufferType.EDITABLE)
    }
}
