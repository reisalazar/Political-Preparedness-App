package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.util.ObjectServices

class VoterInfoFragment : Fragment() {

    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(
            ObjectServices.localRepository,
            ObjectServices.networkRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentVoterInfoBinding.inflate(inflater)
        // Add binding values
        binding.lifecycleOwner = this
        // Add ViewModel values and create ViewModel
        binding.viewModel = viewModel


        // Populate voter info -- hide views without provided data.
        arguments?.let {
            val division = VoterInfoFragmentArgs.fromBundle(it).argDivision
            val electionId = VoterInfoFragmentArgs.fromBundle(it).argElectionId
            viewModel.fetchVoterInfo(division, electionId.toLong())
            viewModel.populateState(electionId.toLong())
            binding.electionId = electionId.toLong()
        }


        // Handle loading of URLs
        viewModel.url.observe(viewLifecycleOwner, Observer {
            loadURLIntent(it)
        })
        // Handle save button UI state
        // cont'd Handle save button clicks
        viewModel.isFollow.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.btnFollow.text = getString(R.string.unfollow_election)
            } else {
                binding.btnFollow.text = getString(R.string.follow_election)
            }
        })

        return binding.root
    }

    // Create method to load URL intents
    private fun loadURLIntent(uri: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }
}