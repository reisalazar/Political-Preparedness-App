package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.udacity.project4.base.BaseFragment


class ElectionsFragment : BaseFragment() {

    // Declare ViewModel
    private lateinit var binding: FragmentElectionBinding
    private val navController by lazy { findNavController() }
    override val viewModel: ElectionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_election,
                container,
                false
            )
        // Add binding values
        binding.lifecycleOwner = this
        // Add ViewModel values and create ViewModel
        binding.viewModel = viewModel

        // Refresh adapters when fragment loads

        // Link elections to voter info
        // Initiate recycler adapters
        // Populate recycler adapters

        //Upcoming Elections
        val upcomingElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            navController.navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election)
            )
        })
        binding.upcomingElectionsRecyclerView.adapter = upcomingElectionAdapter
        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            upcomingElectionAdapter.submitList(it)
        }

        //Saved Elections
        val savedElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            navController.navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
        })

        binding.savedElectionsRecyclerView.adapter = savedElectionAdapter
        viewModel.savedElections.observe(viewLifecycleOwner) {
            savedElectionAdapter.submitList(it)
        }

        return binding.root
    }



}