package com.example.android.politicalpreparedness.data.repository

import com.example.android.politicalpreparedness.domain.NetworkDataSource
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Deferred

class ElectionNetworkRepository(private val apiService: CivicsApiService): NetworkDataSource {

    override suspend fun getListOfElections(): Result<List<Election>> {
        val response = apiService.getElectionsFromNetwork()
        return if (response.isSuccessful) {
            if (response.body() != null) {
                Result.Success(response.body()!!.elections)
            } else {
                Result.Error("No Elections Found", response.code())
            }
        } else {
            Result.Error("Failed to load data", response.code())
        }
    }

    override suspend fun getVotersInfo(address: String, electionId: Long?): Result<VoterInfoResponse> {
        val response = apiService.getVoterInfoFromNetwork(address, electionId)
        return if (response.isSuccessful) {
            if (response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("No Voter Info Found", response.code())
            }
        } else {
            Result.Error("Failed to load data", response.code())
        }
    }

    override fun getRepresentativesFromNetworkAsync(address: Address): Deferred<RepresentativeResponse> {
        return apiService.getRepresentativesFromNetworkAsync(address.toFormattedString())
    }
}