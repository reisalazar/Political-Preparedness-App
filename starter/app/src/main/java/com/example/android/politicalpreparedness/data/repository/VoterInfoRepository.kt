package com.example.android.politicalpreparedness.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.data.SavedElectionDatabase
import com.example.android.politicalpreparedness.data.VoterInfoDatabase
import com.example.android.politicalpreparedness.network.CivicsApiInstance
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VoterInfoRepository(
    private val voterInfoDatabase: VoterInfoDatabase,
    private val savedElectionDatabase: SavedElectionDatabase,
    private val api: CivicsApiInstance
) {

    private val _voterInfo = MutableLiveData<VoterInfo>()
    val voterInfo: LiveData<VoterInfo>
        get() = _voterInfo

    suspend fun getElection(id: Int) : Election?{
        val election = withContext(Dispatchers.IO) {
            return@withContext savedElectionDatabase.get(id)
        }
        return election
    }
    suspend fun insertElection(election: Election) {
        withContext(Dispatchers.IO) {
            savedElectionDatabase.insert(election)
        }
    }
    suspend fun deleteElection(election: Election) {
        withContext(Dispatchers.IO) {
            savedElectionDatabase.delete(election)
        }
    }

    suspend fun refreshVoter(address:String, id:Int) {
        withContext(Dispatchers.IO) {
            val result = api.getVoterInfo(address, id)
            val data = convertToVoterInfo(id, result)
            data?.run {
                voterInfoDatabase.insert(this)
            }
        }
    }

    private fun convertToVoterInfo(id: Int, response: VoterInfoResponse) : VoterInfo? {

        var voterInfo: VoterInfo? = null
        val state = response.state

        if (state?.isNotEmpty() == true) {
            val votingLocatinUrl: String? =
                state[0].electionAdministrationBody.votingLocationFinderUrl?.run {
                this
            }

            val ballotInfoUrl: String? =
                state[0].electionAdministrationBody.ballotInfoUrl?.run {
                this
            }

            voterInfo = VoterInfo(
                id,
                state[0].name,
                votingLocatinUrl,
                ballotInfoUrl
            )
        }

        return voterInfo
    }

    suspend fun loadVoterInfo(id:Int) {
        withContext(Dispatchers.IO) {
            val data = voterInfoDatabase.get(id)
            data?.run {_voterInfo.postValue(this)}
        }
    }
}