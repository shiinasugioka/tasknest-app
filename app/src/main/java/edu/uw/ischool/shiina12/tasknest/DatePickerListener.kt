package edu.uw.ischool.shiina12.tasknest

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar as JavaCalendar

interface DatePickerListener {
    fun onDateSet(year: Int, month: Int, day: Int)
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