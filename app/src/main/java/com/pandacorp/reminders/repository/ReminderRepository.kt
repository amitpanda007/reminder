package com.pandacorp.reminders.repository

import androidx.lifecycle.LiveData
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.data.ReminderDao

class ReminderRepository(private val reminderDao: ReminderDao) {

//    val reminderAllData: LiveData<List<Reminder>> = reminderDao.getAllData()
//    val reminderData: LiveData<List<Reminder>> = reminderDao.getAllDataSortedByDone()
    val reminderData: LiveData<List<Reminder>> = reminderDao.getAllDataSortedByDueDate()
    val completedReminderData: LiveData<List<Reminder>> = reminderDao.getAllCompletedReminder()

    suspend fun addReminder(reminder: Reminder) {
        reminderDao.addReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun deleteAll() {
        reminderDao.deleteAll()
        reminderDao.deleteSequence()
    }

}