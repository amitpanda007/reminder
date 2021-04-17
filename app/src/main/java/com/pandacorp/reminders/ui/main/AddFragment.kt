package com.pandacorp.reminders.ui.main

import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.pandacorp.reminders.LOG_TAG
import com.pandacorp.reminders.R
import com.pandacorp.reminders.data.Priority
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.ui.shared.SharedViewModel
import java.util.*

class AddFragment : Fragment() {

    private lateinit var mSharedViewModel: SharedViewModel
    private var dueDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mSharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        view.findViewById<Button>(R.id.add_btn).setOnClickListener {
            insertReminderToDB()
        }

        // Date button selection
        view.findViewById<ImageButton>(R.id.dateBtn).setOnClickListener {
            Log.i(LOG_TAG, "Date Picker Clicked")
            openDatePicker()
        }

        // Time picker button selection
        view.findViewById<ImageButton>(R.id.timeBtn).setOnClickListener {
            Log.i(LOG_TAG, "Time Picker Clicked")
            openTimePicker()
        }

        return view
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setTitleText("Reminder Date")
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = datePicker.headerText
            val dateTextView = view?.findViewById<TextView>(R.id.datePickerText)
            dateTextView?.text = selectedDate
            dueDate = datePicker.selection!!
        }
    }

    private fun openTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Remind At")
            .build()
        picker.show(childFragmentManager, "TIME_PICKER")

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val min = picker.minute
            val timeTextView = view?.findViewById<TextView>(R.id.timePickerText)
            timeTextView?.text = "$hour:$min"
        }
    }

    private fun insertReminderToDB() {
        var priority: Priority = Priority.HIGH
        val reminderText = view?.findViewById<TextView>(R.id.reminderText)?.text.toString()
        val reminderTime = view?.findViewById<TextView>(R.id.timePickerText)?.text.toString()

        val priorityGroup = view?.findViewById<RadioGroup>(R.id.radioGroup)
        if (priorityGroup != null) {
            when (priorityGroup.checkedRadioButtonId) {
                R.id.high -> priority = Priority.HIGH
                R.id.medium -> priority = Priority.MEDIUM
                R.id.low -> priority = Priority.LOW
                else -> { // Note the block
                    print("Select a Priority")
                }
            }
        }

        if (inputCheck(reminderText)) {

            val reminder = Reminder(
                0, reminderText, priority, Date(dueDate),
                reminderTime, Calendar.getInstance().time, false
            )
            mSharedViewModel.addReminder(reminder)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please provide a proper Reminder", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun inputCheck(reminderText: String): Boolean {
        return !TextUtils.isEmpty(reminderText)
    }

}