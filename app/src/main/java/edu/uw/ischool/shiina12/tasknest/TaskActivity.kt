package edu.uw.ischool.shiina12.tasknest

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar


class TaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var time: EditText
    private lateinit var date: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val timePickerFragment = TimePickerFragment()
        timePickerFragment.setListener(this)

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setListener(this)

        // https://developer.android.com/develop/ui/views/components/pickers
        time = findViewById(R.id.editTextTime)
        date = findViewById(R.id.editTextDate)

        time.setOnClickListener {
            timePickerFragment.show(supportFragmentManager, "timePicker")
        }
        date.setOnClickListener {
            datePickerFragment.show(supportFragmentManager, "datePicker")
        }
    }
    override fun onTimeSet(hourOfDay: Int, minute: Int) {
        // TODO: fix time formatting with ints < 10
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

interface TimePickerListener {
    fun onTimeSet(hourOfDay: Int, minute: Int)
}

interface DatePickerListener {
    fun onDateSet(year: Int, month: Int, day: Int)
}

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private var listener: TimePickerListener? = null

    fun setListener(listener: TimePickerListener) {
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // format time correctly
        listener?.onTimeSet(hourOfDay, minute)
    }
}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var listener: DatePickerListener? = null

    fun setListener(listener: DatePickerListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Log.i("TaskActivity", "datepicker $month/$day/$year")
        listener?.onDateSet(year, month, day)
    }
}