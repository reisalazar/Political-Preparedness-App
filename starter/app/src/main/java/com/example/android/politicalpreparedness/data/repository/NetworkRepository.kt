package com.example.android.politicalpreparedness.data.repository

import com.example.android.politicalpreparedness.domain.NetworkDataSource
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Deferred

class NetworkRepository(private val civicsApiService: CivicsApiService): NetworkDataSource {

    override suspend fun getAllElections(): Result<List<Election>> {
        val response = civicsApiService.getUpcomingElections()
        return if (response.isSuccessful) {
            if (response.body() != null) {
                Result.Success(response.body()!!.elections)
            } else {
                Result.Error("Fail to get elections", response.code())
            }
        } else {
            Result.Error("Failed to load elections", response.code())
        }
    }

    override suspend fun getVotersInfo(address: String, electionId: Long?): Result<VoterInfoResponse> {
        val response = electionId?.let { civicsApiService.getVoterInfo(address, it) }
        return if (response?.isSuccessful == true) {
            if (response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("failed to get voter info", response.code())
            }
        } else {
            Result.Error("failed to load voter info", response?.code())
        }
    }

    override fun getAllRepresentativesAsync(address: Address): Deferred<RepresentativeResponse> {
        return civicsApiService.getAllRepresentativesAsync(address.toFormattedString())
    }
}