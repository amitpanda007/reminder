package com.pandacorp.reminders.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.data.ReminderDatabase
import com.pandacorp.reminders.repository.ReminderRepository
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

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteReminder(reminder)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}