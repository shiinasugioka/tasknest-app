package edu.uw.ischool.shiina12.tasknest

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar as JavaCalendar

interface TimePickerListener {
    fun onTimeSet(hourOfDay: Int, minute: Int, targetEditText: EditText?)
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
        val c = JavaCalendar.getInstance()
        val hour = c.get(JavaCalendar.HOUR_OF_DAY)
        val minute = c.get(JavaCalendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it.
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // format time correctly
        listener?.onTimeSet(hourOfDay, minute, targetEditText)
    }

}