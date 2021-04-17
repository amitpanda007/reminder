package com.pandacorp.reminders.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Insert
    suspend fun insertReminders(reminders: List<Reminder>)

    @Query("DELETE FROM reminder_table")
    suspend fun deleteAll()

    @Query("DELETE FROM reminder_table WHERE id=:id")
    suspend fun deleteReminder(id: Long)
}