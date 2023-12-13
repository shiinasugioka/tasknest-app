package edu.uw.ischool.shiina12.tasknest

import android.accounts.AccountManager
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import edu.uw.ischool.shiina12.tasknest.api_util.ApiAsyncTask
import edu.uw.ischool.shiina12.tasknest.api_util.Constants
import edu.uw.ischool.shiina12.tasknest.api_util.CreateEventTask
import edu.uw.ischool.shiina12.tasknest.util.DatePickerFragment
import edu.uw.ischool.shiina12.tasknest.util.DatePickerListener
import edu.uw.ischool.shiina12.tasknest.util.Task
import edu.uw.ischool.shiina12.tasknest.util.TimePickerFragment
import edu.uw.ischool.shiina12.tasknest.util.TimePickerListener
import edu.uw.ischool.shiina12.tasknest.util.TodoNest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.api.services.calendar.Calendar as GoogleCalendar
import edu.uw.ischool.shiina12.tasknest.util.InMemoryTodoRepository as todoRepo
import edu.uw.ischool.shiina12.tasknest.util.UtilFunctions as Functions

class ViewTaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var currentTask: Task

    private lateinit var eventTitleTextView: EditText
    private lateinit var timeEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var allDay: CheckBox
    private lateinit var repeatingEvent: CheckBox
    private lateinit var startsOn: EditText
    private lateinit var endsOn: EditText
    private lateinit var atTime: EditText
    private lateinit var addEventButton: Button
    private lateinit var apiResultsText: String
    private lateinit var apiStatusText: String
    private lateinit var exitButton: ImageButton

    // values from currentTask object
    private lateinit var currentTaskTitle: String
    private lateinit var currentTaskStartDate: String
    private lateinit var currentTaskStartTime: String

    // values to be sent to API
    private lateinit var eventTitleText: String
    private lateinit var eventStartTimeText: String
    private lateinit var eventStartDateText: String
    private lateinit var finalDateTime: String
    private lateinit var finalTitle: String

    private var mCredential: GoogleAccountCredential? = null  // user's google account
    var mService: GoogleCalendar? = null  // user's google calendar
    var mProgress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_task)

        findAndInitializeUIElements()

        setListeners()
        initCredentials()

    }

    private fun findAndInitializeUIElements() {
        // UI elements
        eventTitleTextView = findViewById(R.id.editTaskTitle)
        timeEditText = findViewById(R.id.editTaskStartTime)
        dateEditText = findViewById(R.id.editTaskStartDate)
        repeatingEvent = findViewById(R.id.checkboxRepeating)
        allDay = findViewById(R.id.allDayCheckBox)
        exitButton = findViewById(R.id.imageButtonExit)

        // UI elements for API
        addEventButton = findViewById(R.id.buttonGoogleCalendar)
        apiResultsText = ""
        apiStatusText = ""

        val currentTaskName = intent.getStringExtra("currentTaskName")
        val dateTaskCreated = intent.getStringExtra("dateCreated")

        Log.d(TAG, "intent task name: $currentTaskName, date: $dateTaskCreated")

        val currNest: TodoNest? = todoRepo.getTodoNestByTitle(todoRepo.getCurrNestName())
        if (currNest != null) {
            for (task in currNest.tasks) {
                Log.d(TAG, "task: ${task.title}, taskdate: ${task.dateCreated}")
                if (task.title == currentTaskName && task.dateCreated == dateTaskCreated) {
                    currentTask = task
                }
            }
        }

        currentTaskTitle = currentTask.title
        currentTaskStartDate = currentTask.displayableStartTime
        currentTaskStartTime = currentTask.displayableStartTime

        eventTitleTextView.setText(currentTaskTitle)
        timeEditText.setText(currentTaskStartTime)
        dateEditText.setText(currentTaskStartDate)

        addEventButton.isEnabled = false
    }

    private fun setListeners() {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.setListener(this, timeEditText)

        val datePickerFragment = DatePickerFragment()
        datePickerFragment.setListener(this, dateEditText)

        timeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                timePickerFragment.show(supportFragmentManager, "timePicker")
            }
        }
        dateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                datePickerFragment.show(supportFragmentManager, "datePicker")
            }
        }

        repeatingEvent.setOnClickListener {
            if (repeatingEvent.isChecked) {
                showCustomDialog()
            }
        }

        allDay.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                timeEditText.visibility = View.VISIBLE
            } else {
                timeEditText.visibility = View.GONE
            }
        }

        exitButton.setOnClickListener {
            finish()
        }

        eventTitleTextView.addTextChangedListener(textWatcher)
        timeEditText.addTextChangedListener(textWatcher)
        dateEditText.addTextChangedListener(textWatcher)

        addEventButton.setOnClickListener {
            Log.i("ViewTaskActivity", "clicked")
            setEventDetails()
            addCalendarEvent()
        }
    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.repeating_dialog_layout, null)
        startsOn = dialogView.findViewById(R.id.repeatingStartDate)
        endsOn = dialogView.findViewById(R.id.repeatingEndDate)
        atTime = dialogView.findViewById(R.id.reminderTime)

        val spinner: Spinner = dialogView.findViewById(R.id.intervalSpinner)
        ArrayAdapter.createFromResource(
            this, R.array.frequency_units_events, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val startDateFragment = DatePickerFragment()
        startDateFragment.setListener(this, startsOn)

        val endDateFragment = DatePickerFragment()
        endDateFragment.setListener(this, endsOn)

        val atTimeFragment = TimePickerFragment()
        atTimeFragment.setListener(this, atTime)

        startsOn.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startDateFragment.show(supportFragmentManager, "datePicker")
            }
        }

        endsOn.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                endDateFragment.show(supportFragmentManager, "datePicker")
            }
        }

        atTime.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                atTimeFragment.show(supportFragmentManager, "timePicker")
            }
        }

        builder.setView(dialogView).setTitle("Options").setPositiveButton("OK") { dialog, _ ->
            // val enteredText = editText.text.toString()
            // Do something with the entered text

            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != RESULT_OK) {
                isGooglePlayServicesAvailable()
            }

            Constants.REQUEST_ACCOUNT_PICKER -> if (data != null) {
                if (resultCode == RESULT_OK && data.extras != null) {
                    val accountName: String? = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    mCredential?.setSelectedAccountName(accountName)
                    val settings = getPreferences(MODE_PRIVATE)
                    val editor = settings.edit()
                    editor.putString(Constants.PREF_ACCOUNT_NAME, accountName)
                    editor.apply()
                } else if (resultCode == RESULT_CANCELED) {
                    apiStatusText = "Account upspecified."
                }
            }

            Constants.REQUEST_AUTHORIZATION -> if (resultCode != RESULT_OK) {
                chooseAccount()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()

        if (isGooglePlayServicesAvailable()) {
            refreshResults()
        } else {
            apiStatusText =
                "Google Play Services required: after installing, close and relaunch this app."
            Log.d(TAG, "api status: $apiStatusText")
        }
    }

    // initialize credentials and user's Google account
    private fun initCredentials() {
        mCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, arrayListOf(CalendarScopes.CALENDAR)
        ).setBackOff(ExponentialBackOff())

        initCalendarBuild(mCredential)
    }

    // initialize user's Google calendar
    private fun initCalendarBuild(credential: GoogleAccountCredential?) {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        mService = GoogleCalendar.Builder(
            transport, jsonFactory, credential
        ).setApplicationName(Constants.APPLICATION_NAME).build()
    }

    private fun addCalendarEvent() {
        CreateEventTask(mService, finalDateTime, finalTitle).execute()
    }

    private fun setEventDetails() {
        eventTitleText = eventTitleTextView.text.toString()
        eventStartTimeText = timeEditText.text.toString()
        eventStartDateText = dateEditText.text.toString()

        val combinedDateTimeString = "$eventStartDateText $eventStartTimeText"
        val formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a")
        val localDateTime = LocalDateTime.parse(combinedDateTimeString, formatter)
        val LAZoneId = ZoneId.of("America/Los_Angeles")
        val formattedDateTime = localDateTime.atZone(LAZoneId)
        val iso8601Formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

//        Log.i(TAG, "iso8601Formatter: $iso8601Formatter")
//        val formattedDateTime = Functions.reformatDate(combinedDateTimeString, "M/d/yyyy h:mm a", "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
//        Log.i(TAG, "formatted: $formattedDateTime")
//        Log.i(TAG, "isoFormatter: ${formattedDateTime.format(iso8601Formatter)}")

        finalDateTime = formattedDateTime.format(iso8601Formatter)
        finalTitle = eventTitleText
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(applicationContext)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun refreshResults() {
        if (mCredential!!.selectedAccountName == null) {
            chooseAccount()
        } else if (!isDeviceOnline()) {
            apiStatusText = "No network connection available"
            Log.d(TAG, "api status: $apiStatusText")
        }

        mProgress?.show()
        ApiAsyncTask(this).execute()
    }

    private fun chooseAccount() {
        startActivityForResult(
            mCredential!!.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER
        )
    }

    private fun isDeviceOnline(): Boolean {
        val connectionManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo

        return (networkInfo != null) && networkInfo.isConnected
    }

    fun clearResultsText() {
        apiStatusText = "Retrieving data..."
        apiResultsText = ""

        Log.d(TAG, "api status: $apiStatusText")
        Log.d(TAG, "api results: $apiResultsText")
    }

    fun updateResultsText(dataStrings: List<String?>?) {
        if (dataStrings == null) {
            apiStatusText = "Error Retrieving data!"
        } else if (dataStrings.isEmpty()) {
            apiStatusText = "No data found."
        } else {
            apiStatusText = "Data retrieved using the Google Calendar API:"
            apiResultsText = TextUtils.join("\n", dataStrings)
            Log.d(TAG, "api status: $apiStatusText")
            Log.d(TAG, "api results: $apiResultsText")
        }
    }

    fun updateStatus(message: String) {
        apiStatusText = message
        Log.d(TAG, "api status: $apiStatusText")
    }

    fun showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode: Int) {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val dialog = apiAvailability.getErrorDialog(
            this, connectionStatusCode, Constants.REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog?.show()
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
                addEventButton.isEnabled = true
            }
        }

    }
}
