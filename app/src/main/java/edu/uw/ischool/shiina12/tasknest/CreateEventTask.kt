package edu.uw.ischool.shiina12.tasknest

import android.os.AsyncTask
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.IOException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CreateEventTask internal constructor(
    private var mService: Calendar?,
    finalDateTime: String,
    finalTitle: String
) :
    AsyncTask<Void?, Void?, Void?>() {

    private val givenStartDateTime = finalDateTime
    private val givenFinalTitle = finalTitle

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Void? {
        addCalendarEvent()
        return null
    }

    private fun addCalendarEvent() {
        // TODO: Replace with user text field input
        val eventTitle = givenFinalTitle

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val formattedStart = ZonedDateTime.parse(givenStartDateTime, formatter)
        val formattedEndTime = formattedStart.plusHours(1)
        val givenEndDateTime = formattedEndTime.format(formatter)

        val eventStartDateTime = givenStartDateTime
        val eventEndDateTime = givenEndDateTime

        val event: Event = Event()
            .setSummary(eventTitle)

        val startDateTime = DateTime(eventStartDateTime)
        val start = EventDateTime()
            .setDateTime(startDateTime)
            .setTimeZone("America/Los_Angeles")
        event.setStart(start)

        val endDateTime = DateTime(eventEndDateTime)
        val end = EventDateTime()
            .setDateTime(endDateTime)
            .setTimeZone("America/Los_Angeles")
        event.setEnd(end)

        val calendarId = "primary"

        try {
            mService?.events()?.insert(calendarId, event)?.execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}