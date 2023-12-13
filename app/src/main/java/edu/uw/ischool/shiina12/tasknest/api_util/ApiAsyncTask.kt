package edu.uw.ischool.shiina12.tasknest.api_util

import android.os.AsyncTask
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import edu.uw.ischool.shiina12.tasknest.ViewTaskActivity

/**
 * This class interacts with the Google Calendar API.
 * It is designed to perform asynchronous tasks in the background
 * for tasks that involve network operations.
 */
class ApiAsyncTask internal constructor(private val mActivity: ViewTaskActivity) :
    AsyncTask<Void?, Void?, Void?>() {

    // Handles exceptions in case the API call fails.
    // Look at Log output for API status and results for more details.
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Void? {
        try {
            mActivity.clearResultsText()
            mActivity.updateResultsText(dataFromApi)
        } catch (availabilityException: GooglePlayServicesAvailabilityIOException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                availabilityException.connectionStatusCode
            )
        } catch (userRecoverableException: UserRecoverableAuthIOException) {
            mActivity.startActivityForResult(
                userRecoverableException.intent, Constants.REQUEST_AUTHORIZATION
            )
        } catch (e: Exception) {
            mActivity.updateStatus(
                """
                    The following error occurred:
                    ${e.message}
                    """.trimIndent()
            )
        }

        if (mActivity.mProgress?.isShowing == true) {
            mActivity.mProgress!!.dismiss()
        }

        return null
    }

    // Retrieves a list of strings representing event summaries and start times
    // from the user's primary Google Calendar.
    private val dataFromApi: List<String>
        get() {
            // list 10 events from the primary calendar
            val eventStrings: MutableList<String> = ArrayList()
            val events: Events? = mActivity.mService!!.events().list("primary").setMaxResults(10)
                .setOrderBy("startTime").setSingleEvents(true).execute()

            val items: MutableList<Event>? = events?.items
            if (items != null) {
                for (event in items) {
                    val start: DateTime = event.start.dateTime
                    eventStrings.add(
                        String.format("%s (%s)", event.summary, start)
                    )
                }
            }

            return eventStrings
        }

}