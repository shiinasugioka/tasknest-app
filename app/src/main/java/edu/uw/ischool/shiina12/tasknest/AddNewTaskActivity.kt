package edu.uw.ischool.shiina12.tasknest

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
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
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo
import edu.uw.ischool.shiina12.tasknest.util.UtilFunctions as Functions

class AddNewTaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var eventTitleTextView: EditText
    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var repeatingEventCheckBox: CheckBox
    private lateinit var repeatingEventLayout: LinearLayout
    private lateinit var repeatingStartDateEditText: EditText
    private lateinit var repeatingEndDateEditText: EditText
    private lateinit var atTimeEditText: EditText
    private lateinit var repeatingIntervalSpinner: Spinner

    private lateinit var currNest: TodoNest
    private lateinit var exitButton: ImageButton
    private lateinit var allDay: CheckBox
    private lateinit var createNewTaskButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_task)

        findAndSetUIElements()
        setListeners()

//        currNest = todoRepo.getTodoNestByTitle("Personal") ?: todoRepo.createTodoList("Personal")

        currNest = todoRepo.getTodoNestByTitle(todoRepo.getCurrNestName())!!

        // Log the details of currNest
        Log.d(
            "AddNewTaskActivity",
            "currNest Title: ${currNest.title}, Number of Tasks: ${currNest.tasks.size}"
        )
    }

    private fun findAndSetUIElements() {
        eventTitleTextView = findViewById(R.id.editTaskTitle)
        timeEditText = findViewById(R.id.editTaskStartTime)
        dateEditText = findViewById(R.id.editTaskStartDate)

        repeatingEventCheckBox = findViewById(R.id.checkboxRepeating)
        repeatingEventLayout = findViewById(R.id.repeatingEventOptions)
        repeatingStartDateEditText = findViewById(R.id.repeatingStartDate)
        repeatingEndDateEditText = findViewById(R.id.repeatingEndDate)
        atTimeEditText = findViewById(R.id.reminderTime)
        allDay = findViewById(R.id.allDayCheckBox)

        exitButton = findViewById(R.id.imageButtonExit)
        createNewTaskButton = findViewById(R.id.createNewTaskButton)

        repeatingIntervalSpinner = findViewById(R.id.intervalSpinner)

        ArrayAdapter.createFromResource(
            this, R.array.frequency_units_events, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            repeatingIntervalSpinner.adapter = adapter
        }
    }

    private fun setListeners() {
        exitButton.setOnClickListener {
            finish()
        }

        createNewTaskButton.setOnClickListener {
            Log.i("Savebtn Test", "Working")
            addTask()
        }

        val timePickerFragment = TimePickerFragment()
        timePickerFragment.setListener(this, timeEditText)

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setListener(this, dateEditText)

        val startDateFragment = DatePickerFragment()
        startDateFragment.setListener(this, repeatingStartDateEditText)

        val endDateFragment = DatePickerFragment()
        endDateFragment.setListener(this, repeatingEndDateEditText)

        val atTimeFragment = TimePickerFragment()
        atTimeFragment.setListener(this, atTimeEditText)

        timeEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                timePickerFragment.show(supportFragmentManager, "timePicker")
            }
        }

        dateEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                datePickerFragment.show(supportFragmentManager, "datePicker")
            }
        }

        repeatingStartDateEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                startDateFragment.show(supportFragmentManager, "datePicker")
            }
        }

        repeatingEndDateEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                endDateFragment.show(supportFragmentManager, "datePicker")
            }
        }

        atTimeEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                atTimeFragment.show(supportFragmentManager, "timePicker")
            }
        }

        repeatingEventCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                repeatingEventLayout.visibility = View.VISIBLE
            } else {
                repeatingEventLayout.visibility = View.INVISIBLE
            }
        }

        allDay.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                timeEditText.visibility = View.VISIBLE
            } else {
                timeEditText.visibility = View.GONE
            }
        }

        eventTitleTextView.addTextChangedListener(textWatcher)
        timeEditText.addTextChangedListener(textWatcher)
        dateEditText.addTextChangedListener(textWatcher)
    }

    private fun addTask() {
        val taskTitle = eventTitleTextView.text.toString()
        val eventStartTime = timeEditText.text.toString()
        val eventStartDate = dateEditText.text.toString()

        val startTimeDate = "$eventStartDate $eventStartTime"
        val finalDateTime =
            Functions.reformatDate(startTimeDate, "M/d/yyyy h:mm a", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        val task = Task(
            title = taskTitle,
            apiDateTime = finalDateTime,
            displayableStartDate = eventStartDate,
            displayableStartTime = eventStartTime
        )

        todoRepo.addTaskToList(currNest, task)
        Log.i(TAG, "result: $task")
        finish()
    }

    override fun onTimeSet(hourOfDay: Int, minute: Int, targetEditText: EditText?) {
        val formattedTime = Functions.getFormattedTimeOnTimeSet(hourOfDay, minute)
        targetEditText?.setText(formattedTime, TextView.BufferType.EDITABLE)
    }

    override fun onDateSet(year: Int, month: Int, day: Int, targetEditText: EditText?) {
        val formattedDate = Functions.getFormattedDateOnDateSet(year, month, day)
        targetEditText?.setText(formattedDate, TextView.BufferType.EDITABLE)
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (eventTitleTextView.text.isNotBlank() && timeEditText.text.isNotBlank() && dateEditText.text.isNotBlank()) {
                createNewTaskButton.isEnabled = true
            }
        }

    }

}
