package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class LaunchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLaunchBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.btnFindMyRepresentatives.setOnClickListener {
            this.findNavController()
                .navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
        }
        binding.btnUpcomingElections.setOnClickListener {
            this.findNavController()
                .navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
        }

        return binding.root
    }


}
