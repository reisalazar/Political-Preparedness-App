package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.repository.LocalRepository
import com.example.android.politicalpreparedness.data.repository.NetworkRepository

// Create Factory to generate VoterInfoViewModel with provided election datasource
@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(
    private val localRepository: Lazy<LocalRepository>,
    private val networkRepository: Lazy<NetworkRepository>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        VoterInfoViewModel(
            localRepository, networkRepository
        ) as T
}