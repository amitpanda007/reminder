package com.pandacorp.reminders.data

import androidx.lifecycle.LiveData

class ReminderRepository(private val reminderDao: ReminderDao) {

    val reminderAllData: LiveData<List<Reminder>> = reminderDao.getAllData()

    suspend fun addReminder(reminder: Reminder) {
        reminderDao.addReminder(reminder)
    }

}