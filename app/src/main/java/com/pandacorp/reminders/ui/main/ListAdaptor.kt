package com.pandacorp.reminders.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pandacorp.reminders.ui.main.ListFragmentDirections
import com.pandacorp.reminders.ui.main.ListFragment
import com.pandacorp.reminders.R
import com.pandacorp.reminders.model.Priority
import com.pandacorp.reminders.data.Reminder
import java.text.SimpleDateFormat
import java.util.*

class ListAdaptor: RecyclerView.Adapter<ListAdaptor.MyViewHolder>()

{
    private var reminderList = emptyList<Reminder>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderId = itemView.findViewById<TextView>(R.id.reminderId)
        val reminderText = itemView.findViewById<TextView>(R.id.reminderText)
        val dueDate = itemView.findViewById<TextView>(R.id.dueDate)
        val dueTime = itemView.findViewById<TextView>(R.id.dueTime)
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

        holder.reminderId.text = currentReminder.id.toString()
        holder.reminderText.text = currentReminder.reminderText.toString()
        holder.dueDate.text = dueDate.toString()
        holder.dueTime.text = currentReminder.dueTime.toString()

        when (currentReminder.priority.name) {
            Priority.HIGH.name -> holder.reminderText.setTextColor(Color.parseColor("#D50000"))
            Priority.MEDIUM.name -> holder.reminderText.setTextColor(Color.parseColor("#0091EA"))
            Priority.LOW.name -> holder.reminderText.setTextColor(Color.parseColor("#00C853"))
            else -> { // Note the block
                print("Select a Priority")
            }
        }

        holder.itemView.findViewById<ConstraintLayout>(R.id.reminderRowLayout).setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentReminder)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(reminders: List<Reminder>) {
        this.reminderList = reminders
        notifyDataSetChanged()
    }
}