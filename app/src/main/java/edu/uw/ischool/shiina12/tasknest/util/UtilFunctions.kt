package edu.uw.ischool.shiina12.tasknest.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Contains commonly used functions across different activities and files
 */
object UtilFunctions {

    fun reformatDate(
        inputDate: String, inputPattern: String, outputPattern: String
    ): String {
        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())

        val date = inputFormat.parse(inputDate) ?: Date()

        return outputFormat.format(date)
    }

    fun getFormattedTimeOnTimeSet(hour: Int, minute: Int): String {
        var correctedHour = hour
        var isAm = true
        if (hour > 12) {
            correctedHour = hour - 12
            isAm = false
        } else if (hour == 0) {
            correctedHour = 12
        }

        var formattedTime: String = if (minute < 10) {
            "$correctedHour:0$minute"
        } else {
            "$correctedHour:$minute"
        }

        formattedTime += if (isAm) " AM" else " PM"

        return formattedTime
    }

    fun getFormattedDateOnDateSet(year: Int, month: Int, day: Int): String {
        val correctedMonth: Int = month + 1

        return "$correctedMonth/$day/$year"
    }

    fun getMillisFromFormattedDateTime(dateTimeString: String): Long {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val date = dateFormat.parse(dateTimeString)
            return date?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}