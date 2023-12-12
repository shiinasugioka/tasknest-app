package edu.uw.ischool.shiina12.tasknest.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * An object that contains commonly used functions across different activities and files
 */
object UtilFunctions {

    /**
     * Given an input date, the input's date pattern, and the desired date pattern,
     * output the original date in the desired format
     */
    fun reformatDate(
        inputDate: String, inputPattern: String, outputPattern: String
    ): String {
        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())

        val date = inputFormat.parse(inputDate) ?: Date()

        return outputFormat.format(date)
    }
}