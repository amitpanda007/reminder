package com.pandacorp.remind.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pandacorp.reminders.R
import com.pandacorp.reminders.ui.main.ListAdaptor
import com.pandacorp.reminders.ui.shared.SharedViewModel

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

        return view
    }
}