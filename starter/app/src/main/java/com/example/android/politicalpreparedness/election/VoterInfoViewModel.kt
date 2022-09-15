package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.SavedElectionDatabase
import com.example.android.politicalpreparedness.data.VoterInfoDatabase
import com.example.android.politicalpreparedness.data.repository.VoterInfoRepository
import com.example.android.politicalpreparedness.network.CivicsApiInstance
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.udacity.project4.base.BaseViewModel
import kotlinx.coroutines.launch

class VoterInfoViewModel(app: Application): BaseViewModel(app) {

    private val repository = VoterInfoRepository(
        VoterInfoDatabase.getInstance(app),
        SavedElectionDatabase.getInstance(app),
        CivicsApiInstance
    )
    val voterInfo = repository.voterInfo

    // Add live data to hold voter info
    private val _selectedElection = MutableLiveData<Election>()
    val selectedElection : LiveData<Election>
        get() = _selectedElection

    private val _isElectionSaved = MutableLiveData<Boolean?>()
    val isElectionSaved : LiveData<Boolean?>
        get() = _isElectionSaved

    private val mockData = true
    val mockVoterInfo = MutableLiveData<VoterInfo>()


    // Add var and methods to populate voter info
    fun refresh(election: Election) {
        _selectedElection.value = election
        refreshIsElectionSaved(election)
        refreshVoterInfo(election)
    }
    private fun refreshIsElectionSaved(data: Election) {
        viewModelScope.launch {
            try {
                val savedElection = repository.getSavedElection(data.id)
                _isElectionSaved.postValue(savedElection != null)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Add var and methods to support loading URLs
    private fun refreshVoterInfo(data: Election) {
        viewModelScope.launch {
            try {
                val state = if(data.division.state.isEmpty()) "ny" else data.division.state
                val address = "${state},${data.division.country}"

                repository.refreshVoterInfo(address, data.id)
                repository.loadVoterInfo(data.id)

            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBarInt.postValue(R.string.no_connection)
                repository.loadVoterInfo(data.id)
            }
        }
    }

    // Add var and methods to save and remove elections to local database
    fun onFollowButtonClick() {
        viewModelScope.launch {
            _selectedElection.value?.let {
                if(isElectionSaved.value == true) {
                    repository.deleteSavedElection(it)
                } else {
                    repository.insertSavedElection(it)
                }
                refreshIsElectionSaved(it)
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    // cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    init {
        if(mockData) {
            val data = VoterInfo(
                2000,
                "State XYZ",
                "",
                "")
            mockVoterInfo.postValue(data)
        }

        _isElectionSaved.value = null
    }

}