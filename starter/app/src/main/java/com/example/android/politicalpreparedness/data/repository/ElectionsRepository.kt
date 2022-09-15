package com.example.android.politicalpreparedness.data.repository

import com.example.android.politicalpreparedness.data.ActiveElectionDatabase
import com.example.android.politicalpreparedness.data.SavedElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(
    private val activeElectionDatabase: ActiveElectionDatabase,
    savedElectionDatabase: SavedElectionDatabase,
    private val api: CivicsApiService) {

    val activeElections = activeElectionDatabase.getAll()
    val savedElections = savedElectionDatabase.getAll()

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            val electionResponse = api.getElections()
            activeElectionDatabase.insertAll(electionResponse.elections)
        }
    }
}