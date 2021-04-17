package com.pandacorp.reminders.ui.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.data.ReminderDatabase
import com.pandacorp.reminders.data.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    val readAllReminder: LiveData<List<Reminder>>
    private val repository: ReminderRepository

    init {
        val reminderDao = ReminderDatabase.getDatabase(app).reminderDao()
        repository = ReminderRepository(reminderDao)
        readAllReminder = repository.reminderAllData
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addReminder(reminder)
        }
    }
}