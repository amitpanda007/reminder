
package com.pandacorp.reminders.ui.main

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.pandacorp.reminders.LOG_TAG
import com.pandacorp.reminders.R
import com.pandacorp.reminders.ReminderBroadcaster
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.model.Priority
import com.pandacorp.reminders.ui.util.Common
import com.pandacorp.reminders.viewmodel.SharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    private val args: UpdateFragmentArgs by navArgs()
    private lateinit var mSharedViewModel: SharedViewModel
    private var dueDate: Long = 0
    private var dueTime: String = ""
    private var chipId: Int = -1
    private var priority: Priority = Priority.NONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)
        view.findViewById<TextInputEditText>(R.id.reminderTextUpdate)
            .setText(args.currentReminder.reminderText)

        //Format Date
        val sdf = SimpleDateFormat("dd MMM yy (EEEE)")
        val netDate = Date(args.currentReminder.dueDate.time)
        val formattedDueDate = sdf.format(netDate)

        dueDate = args.currentReminder.dueDate.time
        view.findViewById<TextInputEditText>(R.id.datePickerTextUpdate).setText(formattedDueDate)

        dueTime = args.currentReminder.dueTime
        view.findViewById<TextInputEditText>(R.id.timePickerTextUpdate)
            .setText(Common.convertToAMPM(args.currentReminder.dueTime))


        priority = args.currentReminder.priority
        when (priority) {
            Priority.HIGH -> view.findViewById<Chip>(R.id.highUpdate).isChecked = true
            Priority.MEDIUM -> view.findViewById<Chip>(R.id.mediumUpdate).isChecked = true
            Priority.LOW -> view.findViewById<Chip>(R.id.lowUpdate).isChecked = true
            else -> { // Note the block
                print("Select a Priority")
            }
        }

        mSharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        view.findViewById<Button>(R.id.update_btn).setOnClickListener {
            updateReminderInDB()
        }

        // Date button selection
        view.findViewById<TextInputLayout>(R.id.textInputLayoutDueDateUpdate)
            .setEndIconOnClickListener {
                Log.i(LOG_TAG, "Date Picker Clicked")
                openDatePicker()
            }

        // Time picker button selection
        view.findViewById<TextInputLayout>(R.id.textInputLayoutDueTimeUpdate)
            .setEndIconOnClickListener {
                Log.i(LOG_TAG, "Time Picker Clicked")
                openTimePicker()
            }

        // Priority Selection Chip
        view.findViewById<ChipGroup>(R.id.chipGroupUpdate)
            .setOnCheckedChangeListener { group, checkedId ->
                // The same checked chip
                if (checkedId == -1) {
                    return@setOnCheckedChangeListener
                }
                chipId = checkedId
            }

        // Add Menu to actionbar
        setHasOptionsMenu(true)

        return view
    }

    private fun openDatePicker() {
        val dateValidator: CalendarConstraints.DateValidator = DateValidatorPointForward.now()
        val dateConstraint = CalendarConstraints.Builder().setValidator(dateValidator).build()

        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setCalendarConstraints(dateConstraint)
            .setTitleText("Reminder Date")
            .build()

        datePicker.show(childFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = datePicker.headerText
            val dateTextView = view?.findViewById<TextView>(R.id.datePickerTextUpdate)
            dateTextView?.text = selectedDate
            dueDate = datePicker.selection!!
        }
    }

    private fun openTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val splitDueTime = dueTime.split(":")
        val selectedHour = splitDueTime[0].toInt()
        val selectedMin = splitDueTime[1].toInt()

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(selectedHour)
            .setMinute(selectedMin)
            .setTitleText("Remind At")
            .build()
        picker.show(childFragmentManager, "TIME_PICKER")

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val min = picker.minute
            dueTime = "$hour:$min"
            val timeTextView = view?.findViewById<TextView>(R.id.timePickerTextUpdate)
            timeTextView?.text = Common.convertToAMPM(dueTime)
        }
    }

    private fun updateReminder() {
        val intent = Intent(context, ReminderBroadcaster::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val reminderData = view?.findViewById<TextView>(R.id.reminderTextUpdate)?.text.toString()
        intent.putExtra("reminder", reminderData)
        intent.putExtra("notificationId", args.currentReminder.intentRequestCode)

        // Remove older Pending Intent
        PendingIntent.getBroadcast(context, args.currentReminder.intentRequestCode, intent, 0).cancel()

        // Create new Pending Intent to save
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, args.currentReminder.intentRequestCode, intent, 0)
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calculate Reminder Time
        val reminderDate = dueDate
        val reminderTime = "$dueTime:00"
        val sdf1 = SimpleDateFormat("MMM dd yyyy")
        val formattedDate = sdf1.format(Date(reminderDate))
        val timeZone = TimeZone.getDefault().displayName
        val dateString = "$formattedDate $reminderTime $timeZone"
        val sdf2 = SimpleDateFormat("MMM dd yyyy HH:mm:ss zzz")
        val date: Date = sdf2.parse(dateString)
        val reminderEpoch = date.time
        Log.i(LOG_TAG, "Reminder Updated for $reminderEpoch")

        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderEpoch, pendingIntent)
    }

    private fun updateReminderInDB() {
        val reminderTextUpdated =
            view?.findViewById<TextView>(R.id.reminderTextUpdate)?.text.toString()

        when (chipId) {
            R.id.highUpdate -> priority = Priority.HIGH
            R.id.mediumUpdate -> priority = Priority.MEDIUM
            R.id.lowUpdate -> priority = Priority.LOW
            else -> { // Note the block
                print("Select a Priority")
            }
        }

        if (inputCheck(reminderTextUpdated)) {

            val updatedReminder = Reminder(
                args.currentReminder.id, reminderTextUpdated, priority, Date(dueDate),
                dueTime, Calendar.getInstance().time, false, args.currentReminder.intentRequestCode
            )
            Log.i(LOG_TAG, updatedReminder.toString())
            mSharedViewModel.updateReminder(updatedReminder)
            Toast.makeText(requireContext(), "Reminder Updated!", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(), "Please provide a proper Reminder", Toast.LENGTH_LONG)
                .show()
        }
        updateReminder()
    }

    private fun inputCheck(reminderTextUpdate: String): Boolean {
        return !TextUtils.isEmpty(reminderTextUpdate)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteReminder()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteReminder() {
        val intent = Intent(context, ReminderBroadcaster::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        // Remove Pending Intent
        PendingIntent.getBroadcast(context, args.currentReminder.intentRequestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT).cancel();


        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mSharedViewModel.deleteReminder(args.currentReminder)
            Toast.makeText(
                requireContext(),
                "Removed: ${args.currentReminder.reminderText}",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete ${args.currentReminder.reminderText}?")
        builder.setMessage("Are you sure you want to delete ${args.currentReminder.reminderText}?")
        builder.create().show()
    }

}