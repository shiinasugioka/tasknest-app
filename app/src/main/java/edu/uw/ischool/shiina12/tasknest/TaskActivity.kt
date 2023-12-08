package edu.uw.ischool.shiina12.tasknest

import android.accounts.AccountManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import edu.uw.ischool.shiina12.tasknest.util.Constants
import com.google.api.services.calendar.Calendar as GoogleCalendar
import java.util.Calendar as JavaCalendar

const val TAG = "TaskActivity"

class TaskActivity : AppCompatActivity(), TimePickerListener, DatePickerListener {
    private lateinit var time: EditText
    private lateinit var date: EditText
    private lateinit var addEventButton: Button
    private lateinit var apiResultsText: String
    private lateinit var apiStatusText: String

    private var mCredential: GoogleAccountCredential? = null  // user's google account
    var mService: GoogleCalendar? = null  // user's google calendar
    var mProgress: ProgressDialog? = null

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


        addEventButton = findViewById(R.id.buttonGoogleCalendar)
        apiResultsText = ""
        apiStatusText = ""

        initCredentials()

        addEventButton.setOnClickListener {
            addCalendarEvent()
        }
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
            apiResultsText = TextUtils.join("\n\n", dataStrings)

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
        val c = JavaCalendar.getInstance()
        val hour = c.get(JavaCalendar.HOUR_OF_DAY)
        val minute = c.get(JavaCalendar.MINUTE)

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
        val c = JavaCalendar.getInstance()
        val year = c.get(JavaCalendar.YEAR)
        val month = c.get(JavaCalendar.MONTH)
        val day = c.get(JavaCalendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Log.i("TaskActivity", "datepicker $month/$day/$year")
        listener?.onDateSet(year, month, day)
    }
}