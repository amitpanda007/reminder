package com.pandacorp.reminders.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_table ORDER BY dueDate ASC")
    fun getAllData(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder_table ORDER BY isDone ASC, dueDate ASC")
    fun getAllDataSortedByDone(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder_table WHERE isDone=0 ORDER BY dueDate ASC")
    fun getAllDataSortedByDueDate(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder_table WHERE isDone=1 ORDER BY dueDate ASC")
    fun getAllCompletedReminder(): LiveData<List<Reminder>>

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