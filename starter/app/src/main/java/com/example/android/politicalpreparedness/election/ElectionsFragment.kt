package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.NavigationCommand
import com.example.android.politicalpreparedness.util.ObjectServices
import com.example.android.politicalpreparedness.util.Status
import com.google.android.material.snackbar.Snackbar


class ElectionsFragment : Fragment() {

    // Declare ViewModel
    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(
            ObjectServices.electionLocalRepository,
            ObjectServices.electionNetworkRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentElectionBinding.inflate(inflater)
        // Add binding values
        binding.lifecycleOwner = this
        // Add ViewModel values and create ViewModel
        binding.viewModel = viewModel

        // Refresh adapters when fragment loads

        // Link elections to voter info
        // Initiate recycler adapters
        val electionAdapter = ElectionListAdapter(object : ElectionListener {
            override fun onClick(election: Election) {
                viewModel.navigationCommand.value = NavigationCommand.To(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                    )
                )
            }
        })
        val savedElectionAdapter = ElectionListAdapter(object : ElectionListener {
            override fun onClick(election: Election) {
                viewModel.navigationCommand.value = NavigationCommand.To(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division
                    )
                )
            }
        })
        // Populate recycler adapters
        binding.upcomingElectionsRecyclerView.adapter = electionAdapter
        binding.savedElectionsRecyclerView.adapter = savedElectionAdapter

        viewModel.fetchUpcomingElection()
        viewModel.fetchSavedElection()

        viewModel.status.observe(viewLifecycleOwner, Observer {
            if (it is Status.ERROR) {
                it.message?.let { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        return binding.root
    }



}