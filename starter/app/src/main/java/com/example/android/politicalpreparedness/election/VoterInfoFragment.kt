package com.example.android.politicalpreparedness.election

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.base.BaseFragment

class VoterInfoFragment : BaseFragment() {

    override val viewModel: VoterInfoViewModel by viewModels()
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_voter_info,
                container,
                false
            )
        // Add binding values
        binding.lifecycleOwner = this
        // Add ViewModel values and create ViewModel
        binding.viewModel = viewModel


        // Populate voter info -- hide views without provided data.
        arguments?.let {
            val arg = VoterInfoFragmentArgs.fromBundle(requireArguments())
            viewModel.refresh(arg.argElectionId)
        }

        // Handle save button UI state
        binding.tvStateHeader.setOnClickListener {
            val urlStr = viewModel.voterInfo.value?.votingLocationUrl
            urlStr?.run {
                startActivityUrlIntent(this)
            }
        }

        binding.tvStateBallot.setOnClickListener {
            val urlStr = viewModel.voterInfo.value?.ballotInformationUrl
            urlStr?.run {
                startActivityUrlIntent(this)
            }
        }
        // cont'd Handle save button clicks
        return binding.root
    }

    // Create method to load URL intents
    // Handle loading of URLs
    private fun startActivityUrlIntent(urlStr: String) {
        val uri: Uri = Uri.parse(urlStr)
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            intent.setPackage("com.android.chrome")
            startActivity(intent)

        } catch (e: ActivityNotFoundException) {

            try {
                intent.setPackage(null)
                startActivity(intent)

            } catch (e: ActivityNotFoundException) {
                val snack = Snackbar.make(
                    requireView(),
                    getString(R.string.failed_to_load_url_to_browser),
                    Snackbar.LENGTH_LONG)
                snack.show()
            }
        }
    }

}
