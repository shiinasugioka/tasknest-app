package edu.uw.ischool.shiina12.tasknest

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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

    private lateinit var colorPalette: ImageView
    private var selectedColorResId: Int? = null
    private lateinit var buttonRed: ImageButton
    private lateinit var buttonOrange: ImageButton
    private lateinit var buttonYellow: ImageButton
    private lateinit var buttonGreen: ImageButton
    private lateinit var buttonBlue: ImageButton
    private lateinit var buttonGray: ImageButton
    private lateinit var buttonPurple: ImageButton
    private lateinit var buttonPink: ImageButton
    private lateinit var selectedColor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_task)

        findAndSetUIElements()
        setListeners()

        currNest = todoRepo.getTodoNestByTitle("Personal") ?: todoRepo.createTodoList("Personal")

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

        findAndSetColorButtons()

        ArrayAdapter.createFromResource(
            this, R.array.frequency_units_events, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            repeatingIntervalSpinner.adapter = adapter
        }
    }

    private fun findAndSetColorButtons() {
        Log.i("AddNewTaskActivity", "setting colors")

        colorPalette = findViewById(R.id.colorPalette)

        val inflater = LayoutInflater.from(this)
        val colorView = inflater.inflate(R.layout.color_picker, null)
        buttonRed = colorView.findViewById(R.id.buttonRed)
        buttonOrange = colorView.findViewById(R.id.buttonOrange)
        buttonYellow = colorView.findViewById(R.id.buttonYellow)
        buttonGreen = colorView.findViewById(R.id.buttonGreen)
        buttonBlue = colorView.findViewById(R.id.buttonBlue)
        buttonGray = colorView.findViewById(R.id.buttonGray)
        buttonPurple = colorView.findViewById(R.id.buttonPurple)
        buttonPink = colorView.findViewById(R.id.buttonPink)
        selectedColor = colorView.findViewById(R.id.selectedColor)

        buttonRed.setOnClickListener {
            selectedColorResId = R.drawable.red
            selectedColor.text = "red selected"
        }
        buttonOrange.setOnClickListener {
            selectedColorResId = R.drawable.orange
            selectedColor.text = "orange selected"
        }
        buttonYellow.setOnClickListener {
            selectedColorResId = R.drawable.yellow
            selectedColor.text = "yellow selected"
        }

        buttonGreen.setOnClickListener {
            selectedColorResId = R.drawable.green
            selectedColor.text = "green selected"
        }

        buttonBlue.setOnClickListener {
            selectedColorResId = R.drawable.blue
            selectedColor.text = "blue selected"
        }

        buttonGray.setOnClickListener {
            selectedColorResId = R.drawable.gray
            selectedColor.text = "gray selected"
        }

        buttonPurple.setOnClickListener {
            selectedColorResId = R.drawable.purple
            selectedColor.text = "purple selected"
        }
        buttonPink.setOnClickListener {
            selectedColorResId = R.drawable.pink
            selectedColor.text = "pink selected"
        }
        colorPalette.setOnClickListener {
            showColorPickerDialog(colorView)
        }
    }

    private fun showColorPickerDialog(colorView: View) {
        val builder = AlertDialog.Builder(this)

        builder.setView(colorView).setTitle("Color options").setPositiveButton("OK") { dialog, _ ->
            // Apply the selected color if available
            selectedColorResId?.let {
                colorPalette.setImageResource(it)
            }
            // Reset the selected color for the next click
            selectedColorResId = null
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ ->
            // Reset the selected color if canceled
            selectedColorResId = null
            dialog.dismiss()
        }.show()
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
        navigateToHomeScreenDayActivity()
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
        Log.i("ViewTaskActivity", "in main $correctedMonth/$day/$year")
        targetEditText?.setText("$correctedMonth/$day/$year", TextView.BufferType.EDITABLE)
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
