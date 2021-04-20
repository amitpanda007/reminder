package com.pandacorp.reminders.ui.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.pandacorp.reminders.LOG_TAG
import com.pandacorp.reminders.R
import com.pandacorp.reminders.ReminderBroadcaster
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.model.Priority
import com.pandacorp.reminders.viewmodel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class AddFragment : Fragment() {

    private lateinit var mSharedViewModel: SharedViewModel
    private var dueDate: Long = 0
    private var selectedDateText = ""
    private var chipId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mSharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        view.findViewById<Button>(R.id.add_btn).setOnClickListener {
            val requestCode = insertReminderToDB()
            createReminder(requestCode)
        }

        // Date button selection
        view.findViewById<TextInputLayout>(R.id.textInputLayoutDueDate).setEndIconOnClickListener {
            Log.i(LOG_TAG, "Date Picker Clicked")
            openDatePicker()
        }

        // Time picker button selection
        view.findViewById<TextInputLayout>(R.id.textInputLayoutDueTime).setEndIconOnClickListener {
            Log.i(LOG_TAG, "Time Picker Clicked")
            openTimePicker()
        }

        // Priority Selection Chip
        view.findViewById<ChipGroup>(R.id.chipGroup).setOnCheckedChangeListener { group, checkedId ->
            // The same checked chip
            if (checkedId == -1) {
                return@setOnCheckedChangeListener
            }
            chipId = checkedId
        }

        return view
    }

    private fun openDatePicker() {
        val dateValidator: DateValidator = DateValidatorPointForward.now()
        val dateConstraint = CalendarConstraints.Builder().setValidator(dateValidator).build()

        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setCalendarConstraints(dateConstraint)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTitleText("Reminder Date")
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener {
            selectedDateText = datePicker.headerText
            val dateTextView = view?.findViewById<TextView>(R.id.datePickerText)
            dateTextView?.text = selectedDateText
            dueDate = datePicker.selection!!
        }
    }

    private fun openTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val rightNow: Calendar = Calendar.getInstance()
        val currentHour: Int = rightNow.get(Calendar.HOUR)
        val currentMin: Int = rightNow.get(Calendar.MINUTE)

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(currentHour)
            .setMinute(currentMin)
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

    private fun createReminder(requestCode: Int) {
        val intent = Intent(context, ReminderBroadcaster::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val reminderData = view?.findViewById<TextView>(R.id.reminderText)?.text.toString()
        intent.putExtra("reminder", reminderData)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calculate Reminder Time
        val reminderDate = dueDate
        val reminderTime = view?.findViewById<TextView>(R.id.timePickerText)?.text.toString() + ":00"
        val sdf1 = SimpleDateFormat("MMM dd yyyy")
        val formattedDate = sdf1.format(Date(reminderDate))
        val timeZone = TimeZone.getDefault().getDisplayName()
        val dateString = "$formattedDate $reminderTime $timeZone"
        val sdf2 = SimpleDateFormat("MMM dd yyyy HH:mm:ss zzz")
        val date: Date = sdf2.parse(dateString)
        val reminderEpoch = date.time
        Log.i(LOG_TAG, "Reminder Set for $reminderEpoch")

        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderEpoch, pendingIntent)
    }

    private fun insertReminderToDB() : Int{
        var priority: Priority = Priority.NONE
        val reminderText = view?.findViewById<TextView>(R.id.reminderText)?.text.toString()
        val reminderTime = view?.findViewById<TextView>(R.id.timePickerText)?.text.toString()

        when (chipId) {
            R.id.high -> priority = Priority.HIGH
            R.id.medium -> priority = Priority.MEDIUM
            R.id.low -> priority = Priority.LOW
            else -> { // Note the block
                print("Select a Priority")
            }
        }

        val requestCode = Random.nextInt(0, 1000) * Random.nextInt(0, 1000)
        if (inputCheck(reminderText)) {

            val reminder = Reminder(
                0, reminderText, priority, Date(dueDate),
                reminderTime, Calendar.getInstance().time, false, requestCode
            )
            Log.i(LOG_TAG, reminder.toString())
            mSharedViewModel.addReminder(reminder)
            Toast.makeText(requireContext(), "New Reminder added!", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please provide a proper Reminder", Toast.LENGTH_LONG)
                .show()
        }
        return requestCode
    }

    private fun inputCheck(reminderText: String): Boolean {
        return !TextUtils.isEmpty(reminderText)
    }

}