package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.shiina12.tasknest.util.DatePickerFragment
import edu.uw.ischool.shiina12.tasknest.util.DatePickerListener
import edu.uw.ischool.shiina12.tasknest.util.Task
import edu.uw.ischool.shiina12.tasknest.util.TimePickerFragment
import edu.uw.ischool.shiina12.tasknest.util.TimePickerListener
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo


class AddNewTaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var time: EditText
    private lateinit var date: EditText
    private lateinit var repeatingEvent: CheckBox
    private lateinit var repeatingEventLayout: LinearLayout
    private lateinit var startsOn: EditText
    private lateinit var endsOn: EditText
    private lateinit var atTime: EditText
    private lateinit var currNest: TodoNest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_task)

        // https://developer.android.com/develop/ui/views/components/pickers
        time = findViewById(R.id.editTextTime)
        date = findViewById(R.id.editTextDate)
        repeatingEvent = findViewById(R.id.checkboxRepeating)
        repeatingEventLayout = findViewById(R.id.repeatingEventOptions)
        startsOn = findViewById(R.id.startsOn)
        endsOn = findViewById(R.id.endsOn)
        atTime = findViewById(R.id.reminderTime)

        currNest = todoRepo.getTodoNestByTitle("Personal") ?: todoRepo.createTodoList("Personal")

        // Log the details of currNest
        Log.d("AddNewTaskActivity", "currNest Title: ${currNest.title}, Number of Tasks: ${currNest.tasks.size}")





        val createTaskButton = findViewById<Button>(R.id.button2)
        createTaskButton.setOnClickListener {
            Log.i("Savebtn Test", "Working")
            addTask()
        }

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
                atTimeFragment.show(supportFragmentManager, "timePicker")
            }
        }

        repeatingEvent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                repeatingEventLayout.visibility = View.VISIBLE
            } else {
                repeatingEventLayout.visibility = View.INVISIBLE
            }
        }

        val spinner: Spinner = findViewById(R.id.intervalSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.frequency_units_events,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun addTask() {
        val taskTitle = findViewById<EditText>(R.id.editTextTask).text.toString()
        val taskDeadline = formatDeadline()

        val task = Task(title = taskTitle, deadline = taskDeadline)
        todoRepo.addTaskToList(currNest, task)

        navigateToHomeScreenDayActivity()
    }


    private fun formatDeadline(): Long {
        val dateString = findViewById<EditText>(R.id.editTextDate).text.toString()

        // Assuming the date format is MM/dd/yyyy
        val formatter = DateTimeFormatter.ofPattern("MM/d/yyyy")
        val localDate = LocalDate.parse(dateString, formatter)

        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun navigateToHomeScreenDayActivity() {
        val intent = Intent(this, HomeScreenDAYActivity::class.java)
        startActivity(intent)
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
