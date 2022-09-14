package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.data.repository.LocalRepository
import com.example.android.politicalpreparedness.data.repository.NetworkRepository
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.NavigationCommand
import com.example.android.politicalpreparedness.util.SingleLiveEvent
import com.example.android.politicalpreparedness.util.Status
import kotlinx.coroutines.launch

// Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val localRepository: Lazy<LocalRepository>,
    private val networkRepository: Lazy<NetworkRepository>

) : ViewModel() {
    private val showToast: SingleLiveEvent<String> = SingleLiveEvent()

    // Create live data val for upcoming elections
    private var _upcomingElections = MutableLiveData<List<Election>>()

    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    // Create live data val for saved elections
    private var _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status





    // Create val and functions to populate live data for upcoming elections from the API
    // and saved elections from local database

    // Create functions to navigate to saved or upcoming election voter info
    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()

    fun fetchUpcomingElection() {
        viewModelScope.launch {
            when (val result = networkRepository.value.getAllElections()) {
                is Result.Success -> {
                    _status.postValue(Status.SUCCESS)
                    _upcomingElections.postValue(result.data)
                }
                is Result.Error -> {
                    _status.postValue(Status.ERROR("Error in fetch Upcoming Election"))
                    showToast.value = "Failed to get elections from network"
                }
            }
        }
    }

    fun fetchSavedElection() {
        viewModelScope.launch {
            viewModelScope.launch {
                when (val result = localRepository.value.getAllElections()) {
                    is Result.Success -> {
                        _status.postValue(Status.SUCCESS)
                        _upcomingElections.postValue(result.data)
                    }
                    is Result.Error -> {
                        _status.postValue(Status.ERROR("Error in fetch Saved Election"))
                        showToast.value = "Failed to get elections from saved elections"
                    }
                }
            }
        }
    }
}