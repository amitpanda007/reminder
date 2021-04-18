package com.pandacorp.reminders.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pandacorp.reminders.R
import com.pandacorp.reminders.ui.main.ListAdaptor
import com.pandacorp.reminders.viewmodel.SharedViewModel

class ListFragment : Fragment() {

    private lateinit var mSharedViewModel: SharedViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        //Recycler View
        val adaptor = ListAdaptor()
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.adapter = adaptor
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // User View Model
        mSharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        mSharedViewModel.readAllReminder.observe(viewLifecycleOwner, Observer { reminder ->
            adaptor.setData(reminder)
        })

        view.findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        // Add Menu
        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete) {
            deleteAllReminders()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllReminders() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_, _ ->
            mSharedViewModel.deleteAll()
            Toast.makeText(requireContext(), "Removed everything", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") {_, _ ->

        }
        builder.setTitle("Delete all reminders?")
        builder.setMessage("Are you sure you want to delete everything?")
        builder.create().show()
    }
}