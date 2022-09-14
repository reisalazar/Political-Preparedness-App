package com.example.android.politicalpreparedness.domain

import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.network.models.Election

interface LocalDataSource {
    suspend fun getAllElections(): Result<List<Election>>

    suspend fun deleteElection(id: Long)

    suspend fun insertElection(election: Election)

    suspend fun getElection(id: Long): Result<Election?>
}