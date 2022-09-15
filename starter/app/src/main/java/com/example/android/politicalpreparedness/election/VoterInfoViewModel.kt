package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.SingleLiveEvent
import com.example.android.politicalpreparedness.util.Status
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val LocalRepository: Lazy<LocalRepository>,
    private val networkRepository: Lazy<NetworkRepository>
) : ViewModel() {


    private val showToast: SingleLiveEvent<String> = SingleLiveEvent()

    // Add live data to hold voter info
    private var _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _isFollow = MutableLiveData<Boolean>()
    val isFollow: LiveData<Boolean>
        get() = _isFollow

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    // Add var and methods to populate voter info
    fun fetchVoterInfo(division: Division, id: Long) {
        viewModelScope.launch {
            _status.value = Status.LOADING
            if (division.state.isNotEmpty() && division.country.isNotEmpty()) {
                val address = "${division.country},${division.state}"
                when (val result = networkRepository.value.getVotersInfo(address, id)) {
                    is Result.Success -> {
                        _status.value = Status.SUCCESS
                        _voterInfo.value = result.data
                    }
                    is Result.Error -> {
                        _status.value = Status.ERROR("Error in fetch Voter info")
                        showToast.value = "Failed to get voter info"
                    }
                }
            } else {
                _status.value = Status.ERROR("Error in load division country and state")
                showToast.value = "Failed to get division country and state"
            }
        }
    }

    // Add var and methods to support loading URLs
    fun loadURLs(url: String) {
        _url.value = url
    }

    // Add var and methods to save and remove elections to local database
    fun electionFollow(electionId: Long) {
        viewModelScope.launch {
            kotlin.runCatching {
                LocalRepository.value.getElection(electionId)
            }.onSuccess {
                when (it) {
                    is Result.Success -> {
                        LocalRepository.value.deleteElection(electionId)
                        _isFollow.value = false
                    }
                    is Result.Error -> {
                        if (voterInfo.value != null) {
                            LocalRepository.value.insertElection(voterInfo.value?.election!!)
                            _isFollow.value = true
                        } else {
                            _status.value = Status.ERROR("Error on follow the Election")
                            _isFollow.value = false
                        }
                    }
                }
            }.onFailure {
                _status.value = Status.ERROR("Error in getting election by id from repository")
                showToast.value = "Failed to get election from repository"
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    // cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    fun populateState(electionId: Long) {
        viewModelScope.launch {
            LocalRepository.value.getElection(electionId).runCatching {
                when (this) {
                    is Result.Error -> {
                        _isFollow.value = false
                    }
                    is Result.Success -> {
                        _isFollow.value = true
                    }
                }
            }.onFailure {
                _status.value = Status.ERROR("Error in Loading page")
            }
        }
    }

}