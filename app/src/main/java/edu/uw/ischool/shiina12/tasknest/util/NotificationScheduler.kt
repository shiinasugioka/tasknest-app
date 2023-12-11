package edu.uw.ischool.shiina12.tasknest.util

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import edu.uw.ischool.shiina12.tasknest.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler {

    // TODO: Call MyNotificationScheduler.scheduleNotification when adding a new task to the nest

    /* Sample code

        // Simulate an event time (e.g., current time + 10 seconds)
        val eventTimeInMillis = System.currentTimeMillis() + 10000

        // Immediately show a test notification
        NotificationScheduler().scheduleNotification(this, eventTimeInMillis)
     */

    private val tag = "NotificationScheduler"

    fun scheduleNotification(context: Context, eventTimeInMillis: Long) {
        // Read preferences to get notification time before the event
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val notificationTimeBeforeEvent = sharedPreferences.getString("notification_frequency", "1")
            ?.toInt()
        val frequencyUnit = sharedPreferences.getString("frequency_unit", "minutes")

        Log.i(tag, "Preference Frequency $notificationTimeBeforeEvent ")
        Log.i(tag, "Preference Frequency Unit $frequencyUnit ")


        // Calculate the notification time based on preferences
        val notificationTimeInMillis = calculateNotificationTime(eventTimeInMillis, notificationTimeBeforeEvent, frequencyUnit)
        Log.i(tag, "TotalNotifTime $notificationTimeInMillis ")


        // Schedule an alarm to trigger the notification
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        // Set the alarm based on the calculated notification time
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            notificationTimeInMillis,
            pendingIntent
        )
    }

    private fun calculateNotificationTime(eventTimeInMillis: Long, timeBeforeEvent: Int?, frequencyUnit: String?): Long {
        // Convert timeBeforeEvent to milliseconds based on the specified frequency unit
        val timeBeforeEventInMillis = when (frequencyUnit) {
            "hours" -> TimeUnit.HOURS.toMillis(timeBeforeEvent!!.toLong())
            "minutes" -> TimeUnit.MINUTES.toMillis(timeBeforeEvent!!.toLong())
            "days" -> TimeUnit.DAYS.toMillis(timeBeforeEvent!!.toLong())
            else -> 0 // Default to 0 if an unsupported unit is provided
        }

        // Calculate the notification time by subtracting timeBeforeEventInMillis from eventTimeInMillis
        return eventTimeInMillis - timeBeforeEventInMillis
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Create and display the notification
        context?.let {
            val notification = createNotification(it)
            showNotification(it, notification)
        }
    }

    private fun createNotification(context: Context): Notification? {
        // Read preferences to check if each notification type is enabled
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val appNotificationsEnabled = sharedPreferences.getBoolean("notification_app", false)
        val smsNotificationsEnabled = sharedPreferences.getBoolean("notification_sms", false)
        val emailNotificationsEnabled = sharedPreferences.getBoolean("notification_email", false)

        // Customize the notification based on the enabled channels
        val builder = NotificationCompat.Builder(context, "app_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Event Notification")
            // TODO: Maybe add event title
            .setContentText("Your event is about to start!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Check preferences and add channels accordingly
        if (appNotificationsEnabled) {
            builder.setChannelId("app_channel")
        }

        if (smsNotificationsEnabled) {
            builder.setChannelId("sms_channel")
        }

        if (emailNotificationsEnabled) {
            builder.setChannelId("email_channel")
        }

        return builder.build()
    }

    private fun showNotification(context: Context, notification: Notification?) {
        // Show the notification using NotificationManagerCompat
        notification?.let {
            val notificationManager = NotificationManagerCompat.from(context)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.FOREGROUND_SERVICE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Handle the case where the permission is not granted.
                return
            }

            notificationManager.notify(1, it)
        }
    }
}
