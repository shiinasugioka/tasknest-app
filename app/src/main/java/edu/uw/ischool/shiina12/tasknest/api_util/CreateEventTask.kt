package edu.uw.ischool.shiina12.tasknest.api_util

import android.os.AsyncTask
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.IOException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import edu.uw.ischool.shiina12.tasknest.util.UtilFunctions as Functions

/**
 * This class contains the actual data that is passed into the API.
 * Given user input, the API will add an event to the user's Google
 * calendar account.
 *
 * If there are problems with the API it probably isn't this file.
 **/
class CreateEventTask internal constructor(
    private var mService: Calendar?,
    finalDateTime: String,
    finalTitle: String,
    finalRepeatEnd: String,
    finalRepeatingInterval: String
) : AsyncTask<Void?, Void?, Void?>() {

    private val givenStartDateTime = finalDateTime
    private val givenFinalTitle = finalTitle
    private val givenRepeatEnd = finalRepeatEnd
    private val repeatingInterval = finalRepeatingInterval

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Void? {
        addCalendarEvent()
        return null
    }

    private fun addCalendarEvent() {
        val eventTitle = givenFinalTitle

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val formattedStart = ZonedDateTime.parse(givenStartDateTime, formatter)
        val formattedEndTime = formattedStart.plusHours(1)
        val givenEndDateTime = formattedEndTime.format(formatter)

        val eventStartDateTime = givenStartDateTime

        val event: Event = Event().setSummary(eventTitle)

        val startDateTime = DateTime(eventStartDateTime)
        val start = EventDateTime().setDateTime(startDateTime).setTimeZone("America/Los_Angeles")
        event.setStart(start)

        val endDateTime = DateTime(givenEndDateTime)
        val end = EventDateTime().setDateTime(endDateTime).setTimeZone("America/Los_Angeles")
        event.setEnd(end)

        if (givenRepeatEnd != "" && repeatingInterval != "") {
            val repeatEnd = DateTime(givenRepeatEnd)
            val rEnd = EventDateTime().setDateTime(repeatEnd)
                .setTimeZone("America/Los_Angeles").dateTime.toString()

            val formattedREnd =
                Functions.reformatDate(rEnd, "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyyMMdd'T'HHmmss'Z'")


            var rInterval = "WEEKLY"
            if (repeatingInterval == "Daily") {
                rInterval = "DAILY"
            } else if (repeatingInterval == "Monthly") {
                rInterval = "MONTHLY"
            }

            val recurrence = "RRULE:FREQ=$rInterval;UNTIL=$formattedREnd"
            event.setRecurrence(listOf(recurrence));
        }

        val calendarId = "primary"

        try {
            mService?.events()?.insert(calendarId, event)?.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}