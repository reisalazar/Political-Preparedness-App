package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//D: Create Factory to generate ElectionViewModel with provided election datasource
@Suppress("UNCHECKED_CAST")
class ElectionsViewModelFactory(
    private val localDataRepository: Lazy<ElectionLocalDataRepository>,
    private val networkDataRepository: Lazy<ElectionNetworkDataRepository>
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ElectionsViewModel(
        localDataRepository, networkDataRepository
    ) as T
}