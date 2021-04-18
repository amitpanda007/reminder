package com.pandacorp.reminders.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Insert
    suspend fun insertReminders(reminders: List<Reminder>)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminder_table")
    suspend fun deleteAll()

    @Query("DELETE FROM sqlite_sequence WHERE name= 'reminder_table'")
    suspend fun deleteSequence()
}