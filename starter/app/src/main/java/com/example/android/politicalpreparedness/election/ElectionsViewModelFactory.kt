package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.repository.LocalRepository
import com.example.android.politicalpreparedness.data.repository.NetworkRepository

// Create Factory to generate ElectionViewModel with provided election datasource
@Suppress("UNCHECKED_CAST")
class ElectionsViewModelFactory(
    private val localDataRepository: Lazy<LocalRepository>,
    private val networkDataRepository: Lazy<NetworkRepository>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ElectionsViewModel(
        localDataRepository, networkDataRepository
    ) as T
}