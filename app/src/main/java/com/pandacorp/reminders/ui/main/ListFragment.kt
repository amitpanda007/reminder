package com.pandacorp.reminders.ui.main

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.pandacorp.reminders.LOG_TAG
import com.pandacorp.reminders.R
import com.pandacorp.reminders.ReminderBroadcaster
import com.pandacorp.reminders.data.Reminder
import com.pandacorp.reminders.viewmodel.SharedViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class ListFragment : Fragment(), ListAdaptor.OnItemClick {

    private lateinit var mSharedViewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adaptor: ListAdaptor
    private lateinit var allReminders: List<Reminder>
    private lateinit var removedReminder: Reminder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        //Recycler View
        adaptor = ListAdaptor()
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.adapter = adaptor
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // User View Model
        mSharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        mSharedViewModel.readAllReminder.observe(viewLifecycleOwner, Observer { reminder ->
            adaptor.setData(reminder, this)
            allReminders = reminder
        })

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment, null,
                navOptions { // Use the Kotlin DSL for building NavOptions
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                    }
                })
        }

        // Add Menu
        setHasOptionsMenu(true)

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private var simpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var position = viewHolder.adapterPosition
            when(direction) {
                ItemTouchHelper.LEFT -> {
                    removedReminder = adaptor.getData().get(position)
                    adaptor.removeReminder(position)

                    Snackbar.make(recyclerView, "Reminder temporarily deleted", Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo",
                            View.OnClickListener {
                                adaptor.addReminder(position, removedReminder)
                            }).show()
                }
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            RecyclerViewSwipeDecorator.Builder(
                c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
            )
                .addBackgroundColor(ContextCompat.getColor(context!!, R.color.delete_background))
                .addActionIcon(R.drawable.ic_delete_24)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteAllReminders()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllReminders() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mSharedViewModel.readAllReminder.observe(viewLifecycleOwner, Observer { reminder ->
                for (item in reminder) {
                    Log.i(LOG_TAG, "Removing intent with Code: ${item.intentRequestCode}")
                    val intent = Intent(context, ReminderBroadcaster::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    // Remove older Pending Intent
                    PendingIntent.getBroadcast(context, item.intentRequestCode, intent, 0).cancel()
                }
            })
            mSharedViewModel.deleteAll()
            Toast.makeText(requireContext(), "Removed everything", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { _, _ ->

        }
        builder.setTitle("Delete all reminders?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }

    override fun onClick(reminder: Reminder) {
        val done = !reminder.isDone
        val updatedReminder = Reminder(
            reminder.id,
            reminder.reminderText,
            reminder.priority,
            reminder.dueDate,
            reminder.dueTime,
            reminder.dateCreated,
            done,
            reminder.intentRequestCode
        )
        mSharedViewModel.updateReminder(updatedReminder)
        adaptor.updateReminderFlag()
    }
}