package edu.uw.ischool.shiina12.tasknest

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

interface TimePickerListener {
    fun onTimeSet(hourOfDay: Int, minute: Int, targetEditText: EditText?)
}

interface DatePickerListener {
    fun onDateSet(year: Int, month: Int, day: Int, targetEditText: EditText?)
}

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private var listener: TimePickerListener? = null
    private var targetEditText: EditText? = null

    fun setListener(listener: TimePickerListener, targetEditText: EditText) {
        this.listener = listener
        this.targetEditText = targetEditText

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // format time correctly
        listener?.onTimeSet(hourOfDay, minute, targetEditText)
    }

}

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var listener: DatePickerListener? = null
    private var targetEditText: EditText? = null

    fun setListener(listener: DatePickerListener, targetEditText: EditText) {
        this.listener = listener
        this.targetEditText = targetEditText
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        // Log.i("TaskActivity", "datepicker $month/$day/$year")
        listener?.onDateSet(year, month, day, targetEditText)
    }
}
