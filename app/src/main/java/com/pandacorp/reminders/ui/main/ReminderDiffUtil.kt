package com.pandacorp.reminders.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.pandacorp.reminders.data.Reminder

class ReminderDiffUtil (
    private val oldList: List<Reminder>,
    private val newList: List<Reminder>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].reminderText == newList[newItemPosition].reminderText
                && oldList[oldItemPosition].dueDate == newList[newItemPosition].dueDate
                && oldList[oldItemPosition].dueTime == newList[newItemPosition].dueTime
                && oldList[oldItemPosition].repeat == newList[newItemPosition].repeat
                && oldList[oldItemPosition].isDone == newList[newItemPosition].isDone
                && oldList[oldItemPosition].priority == newList[newItemPosition].priority
    }

}