package com.example.android.politicalpreparedness.data.repository

import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.domain.LocalDataSource
import com.example.android.politicalpreparedness.network.models.Election

class ElectionLocalRepository(private val electionDao: Lazy<ElectionDao>): LocalDataSource {
    override suspend fun getListOfElections(): Result<List<Election>> {
        val items = electionDao.value.getListOfElections()
        return if (items == null) {
            Result.Success(emptyList())
        } else {
            Result.Success(items)
        }
    }

    override suspend fun getElectionById(id: Long): Result<Election?> {
        val item = electionDao.value.getElectionById(id)
        return if (item != null) {
            Result.Success(item)
        } else {
            Result.Error("Item doesn't exist")
        }
    }

    override suspend fun deleteElection(id: Long) {
        electionDao.value.removeElectionById(id)
    }

    override suspend fun saveElection(election: Election) {
        electionDao.value.saveElection(election)
    }
}