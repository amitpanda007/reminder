package com.pandacorp.reminders.ui.util

import androidx.room.TypeConverter
import com.pandacorp.reminders.model.Priority
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromPriority(value: Priority?): String? {
        return value?.let { it.name }
    }

    @TypeConverter
    fun toPriority(priority: String?): Priority? {
        return priority?.let { Priority.valueOf(it) }
    }
}