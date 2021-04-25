package com.pandacorp.reminders.ui.main

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.reminders.LOG_TAG
import com.pandacorp.reminders.R
import com.pandacorp.reminders.model.Priority
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.ui.util.Common
import java.text.SimpleDateFormat
import java.util.*

class ListAdaptor: RecyclerView.Adapter<ListAdaptor.MyViewHolder>()

{
    private var reminderList = mutableListOf<Reminder>()
    private lateinit var onItemClick: OnItemClick

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderId = itemView.findViewById<TextView>(R.id.reminderId)
        val reminderText = itemView.findViewById<TextView>(R.id.reminderText)
        val dueDate = itemView.findViewById<TextView>(R.id.dueDate)
        val dueTime = itemView.findViewById<TextView>(R.id.dueTime)
        val repeatText = itemView.findViewById<TextView>(R.id.repeatText)
        val repeatImage = itemView.findViewById<ImageButton>(R.id.repeatImageButton)
        val done = itemView.findViewById<RadioButton>(R.id.done)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.reminder_grid_item, parent, false))
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentReminder = reminderList[position]

        //Format Date
        val sdf = SimpleDateFormat("dd MMM yy (EEEE)")
        val netDate = Date(currentReminder.dueDate.time)
        val dueDate = sdf.format(netDate)

//        holder.reminderId.text = currentReminder.id.toString()
        holder.reminderId.text = (position + 1).toString()
        holder.reminderText.text = currentReminder.reminderText
        holder.dueDate.text = dueDate.toString()
        holder.dueTime.text = Common.convertToAMPM(currentReminder.dueTime)
        holder.repeatText.text = currentReminder.repeat
        holder.done.isChecked = currentReminder.isDone

        if(currentReminder.repeat.contentEquals("OFF")) {
            holder.repeatImage.setColorFilter(R.color.black, PorterDuff.Mode.MULTIPLY)
        }

        if(currentReminder.isDone) {
            Log.i(LOG_TAG, "Setting Text Strike through")
            holder.reminderText.paintFlags = holder.reminderText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else {
            Log.i(LOG_TAG, "Resetting Text Strike through")
            holder.reminderText.paintFlags = holder.reminderText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        when (currentReminder.priority.name) {
            Priority.HIGH.name -> holder.reminderText.setTextColor(Color.parseColor("#FFD50000"))
            Priority.MEDIUM.name -> holder.reminderText.setTextColor(Color.parseColor("#FF0091EA"))
            Priority.LOW.name -> holder.reminderText.setTextColor(Color.parseColor("#FF00C853"))
            else -> { // Note the block
                print("Select a Priority")
            }
        }

        holder.itemView.findViewById<ConstraintLayout>(R.id.reminderRowLayout).setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentReminder)
            holder.itemView.findNavController().navigate(action, navOptions {
                anim {
                    enter = android.R.animator.fade_in
                    exit = android.R.animator.fade_out
                }
            })
        }

        holder.itemView.findViewById<RadioButton>(R.id.done).setOnClickListener {
            holder.done.isChecked = !currentReminder.isDone
            onItemClick.onClick(currentReminder)
        }
    }

    fun setData(reminders: List<Reminder>, onItemClick: OnItemClick) {
        this.reminderList = reminders as MutableList<Reminder>
        this.onItemClick = onItemClick
        notifyDataSetChanged()
    }

    fun getData(): List<Reminder> {
        return this.reminderList
    }

    fun removeReminder(position: Int) {
        this.reminderList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addReminder(position: Int, reminder: Reminder) {
        this.reminderList.add(position, reminder)
        notifyItemInserted(position)
    }

    fun updateReminderFlag() {
        notifyDataSetChanged()
    }

    interface OnItemClick{
        fun onClick(reminder: Reminder)
    }
}