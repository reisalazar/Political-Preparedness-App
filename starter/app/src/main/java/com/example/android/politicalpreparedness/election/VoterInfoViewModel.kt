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
    private val _election = MutableLiveData<Election>()
    val election : LiveData<Election>
        get() = _election

    private val _electionSaved = MutableLiveData<Boolean?>()
    val electionSaved : LiveData<Boolean?>
        get() = _electionSaved

    private val mock = true
    private val mockVoterInfo = MutableLiveData<VoterInfo>()


    // Add var and methods to populate voter info
    fun refreshElections(election: Election) {
        _election.value = election
        refreshIsElectionSaved(election)
        refreshVoterInfo(election)
    }
    private fun refreshIsElectionSaved(electionData: Election) {
        viewModelScope.launch {
            try {
                val election = repository.getElection(electionData.id)
                _electionSaved.postValue(election != null)

            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }


    // Add var and methods to support loading URLs
    private fun refreshVoterInfo(electionData: Election) {
        viewModelScope.launch {
            try {
                val state = electionData.division.state.ifEmpty { "ny" }
                val address = "${state},${electionData.division.country}"

                repository.refreshVoter(address, electionData.id)
                repository.loadVoterInfo(electionData.id)

            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBarInt.postValue(R.string.no_connection)
                repository.loadVoterInfo(electionData.id)
            }
        }
    }

    // Add var and methods to save and remove elections to local database
    fun onFollowButtonClick() {
        viewModelScope.launch {
            _election.value?.let {
                if(this@VoterInfoViewModel.electionSaved.value == true) {
                    repository.deleteElection(it)
                } else {
                    repository.insertElection(it)
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
        if(mock) {
            val data = VoterInfo(
                2000,
                "State XYZ",
                "",
                "")
            mockVoterInfo.postValue(data)
        }

        _electionSaved.value = null
    }

}