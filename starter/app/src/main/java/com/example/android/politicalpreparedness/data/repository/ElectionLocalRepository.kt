package com.example.android.politicalpreparedness.data.repository

import com.example.android.politicalpreparedness.data.Result
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.domain.LocalDataSource
import com.example.android.politicalpreparedness.network.models.Election

class ElectionLocalRepository(private val electionDao: Lazy<ElectionDao>): LocalDataSource {
    override suspend fun getAllElections(): Result<List<Election>> {
        val items = electionDao.value.getAllElections()
        return if (items == null) {
            Result.Success(emptyList())
        } else {
            Result.Success(items)
        }
    }

    override suspend fun getElection(id: Long): Result<Election?> {
        val item = electionDao.value.get(id)
        return Result.Success(item)
    }

    override suspend fun deleteElection(id: Long) {
        electionDao.value.delete(id)
    }

    override suspend fun insertElection(election: Election) {
        electionDao.value.insert(election)
    }
}