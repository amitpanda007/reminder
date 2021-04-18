package com.pandacorp.reminders.data

import android.os.Parcelable
import java.util.Date;
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.pandacorp.reminders.model.Priority



@Parcelize
@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var reminderText: String,
    var priority: Priority,
    var dueDate: Date,
    var dueTime: String,
    var dateCreated: Date,
    var isDone: Boolean
) : Parcelable {
    override fun toString(): String {
        return "Reminder(reminderId=$id, reminder=$reminderText, priority=$priority, dueDate=$dueDate, dueTime=$dueTime, dateCreated=$dateCreated, isDone=$isDone)"
    }
}



