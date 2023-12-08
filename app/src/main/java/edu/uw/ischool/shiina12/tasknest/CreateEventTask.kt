package edu.uw.ischool.shiina12.tasknest

import android.os.AsyncTask
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.IOException

class CreateEventTask internal constructor(private var mService: Calendar?) :
    AsyncTask<Void?, Void?, Void?>() {

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Void? {
        addCalendarEvent()
        return null
    }

    private fun addCalendarEvent() {
        // TODO: Replace with user text field input
        val eventTitle = "Sample New Event"
        val eventLocation = "1410 NE Campus Pkwy, Seattle, WA 98195"
        val eventDescription = "This is a sample event to make sure the app works"

        val eventStartDateTime = "2023-12-7T09:00:00-07:00"
        val eventEndDateTime = "2023-12-7T17:00:00-07:00"

        val event: Event = Event()
            .setSummary(eventTitle)
            .setLocation(eventLocation)
            .setDescription(eventDescription)

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