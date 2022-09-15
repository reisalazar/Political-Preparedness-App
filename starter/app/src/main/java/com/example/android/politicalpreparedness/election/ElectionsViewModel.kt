package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
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

    // Create functions to navigate to saved or upcoming election voter info
    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()

    // Create val and functions to populate live data for upcoming elections from the API
    // and saved elections from local database
    fun fetchUpcomingElection() {
        viewModelScope.launch {
            _status.postValue(Status.LOADING)
            kotlin.runCatching {
                networkRepository.value.getAllElections()
            }.onSuccess {
                when (it) {
                    is Result.Success -> {
                        _status.postValue(Status.SUCCESS)
                        _upcomingElections.postValue(it.data)
                    }
                    is Result.Error -> {
                        _status.postValue(Status.ERROR("Error in fetch Upcoming Election"))
                        showToast.value = "Failed to get elections from network"
                    }
                }
            }.onFailure {
                _status.postValue(Status.ERROR("Failed at the network repository get all elections"))
            }
        }
    }

    fun fetchSavedElection() {
        viewModelScope.launch {
            kotlin.runCatching {
                localRepository.value.getAllElections()
            }.onSuccess {
                when (it) {
                    is Result.Success -> {
                        _status.postValue(Status.SUCCESS)
                        _upcomingElections.postValue(it.data)
                    }
                    is Result.Error -> {
                        _status.postValue(Status.ERROR("Error in fetch Saved Election"))
                        showToast.value = "Failed to get elections from saved elections"
                    }
                }
            }.onFailure {
                _status.postValue(Status.ERROR("Failed at the local repository get all elections"))

            }
        }
    }
}