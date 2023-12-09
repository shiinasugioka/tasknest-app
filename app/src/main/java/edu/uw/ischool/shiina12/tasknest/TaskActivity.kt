package edu.uw.ischool.shiina12.tasknest

import android.accounts.AccountManager
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
import edu.uw.ischool.shiina12.tasknest.util.Constants
import com.google.api.services.calendar.Calendar as GoogleCalendar

const val TAG = "TaskActivity"

class TaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var time: EditText
    private lateinit var date: EditText
    private lateinit var allDay: CheckBox
    private lateinit var repeatingEvent: CheckBox
    private lateinit var startsOn: EditText
    private lateinit var endsOn: EditText
    private lateinit var atTime: EditText
    private lateinit var addEventButton: Button
    private lateinit var apiResultsText: String
    private lateinit var apiStatusText: String

    private var mCredential: GoogleAccountCredential? = null  // user's google account
    var mService: GoogleCalendar? = null  // user's google calendar
    var mProgress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // https://developer.android.com/develop/ui/views/components/pickers
        time = findViewById(R.id.editTextTime)
        date = findViewById(R.id.editTextDate)
        repeatingEvent = findViewById(R.id.checkboxRepeating)
        allDay = findViewById(R.id.allDayCheckBox)

        repeatingEvent.setOnClickListener {
            if (repeatingEvent.isChecked) {
                showCustomDialog()
            }
        }

        addEventButton = findViewById(R.id.buttonGoogleCalendar)
        apiResultsText = ""
        apiStatusText = ""

        initCredentials()

        addEventButton.setOnClickListener {
            addCalendarEvent()
        }
    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.repeating_dialog_layout, null)
        startsOn = dialogView.findViewById(R.id.startsOn)
        endsOn = dialogView.findViewById(R.id.endsOn)
        atTime = dialogView.findViewById(R.id.reminderTime)

        val spinner: Spinner = dialogView.findViewById(R.id.intervalSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.frequency_units_events,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        //val editText = dialogView.findViewById<EditText>(R.id.editText)
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

    @Deprecated("Deprecated in Java")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != RESULT_OK) {
                isGooglePlayServicesAvailable()
            }

            Constants.REQUEST_ACCOUNT_PICKER -> if (data != null) {
                if (resultCode == RESULT_OK && data.extras != null) {
                    val accountName: String? =
                        data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
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
            applicationContext,
            arrayListOf(CalendarScopes.CALENDAR)
        )
            .setBackOff(ExponentialBackOff())

        initCalendarBuild(mCredential)
    }

    // initialize user's Google calendar
    private fun initCalendarBuild(credential: GoogleAccountCredential?) {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        mService = GoogleCalendar.Builder(
            transport, jsonFactory, credential
        )
            .setApplicationName(Constants.APPLICATION_NAME)
            .build()
    }

    private fun addCalendarEvent() {
        CreateEventTask(mService).execute()
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode =
            apiAvailability.isGooglePlayServicesAvailable(applicationContext)
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
            mCredential!!.newChooseAccountIntent(),
            Constants.REQUEST_ACCOUNT_PICKER
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
            this,
            connectionStatusCode,
            Constants.REQUEST_GOOGLE_PLAY_SERVICES
        )
        dialog?.show()
    }

    override fun onTimeSet(hourOfDay: Int, minute: Int, targetEditText: EditText?) {
        var correctedHour = hourOfDay
        var isAm = true
        if (hourOfDay > 12) {
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
