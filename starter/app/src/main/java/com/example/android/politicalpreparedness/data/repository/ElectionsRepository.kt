package com.example.android.politicalpreparedness.data.repository

import com.example.android.politicalpreparedness.data.UpcomingElectionDatabase
import com.example.android.politicalpreparedness.data.SavedElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(
    private val upcomingElectionDatabase: UpcomingElectionDatabase,
    savedElectionDatabase: SavedElectionDatabase,
    private val api: CivicsApiInstance
) {

    val upcomingElections = upcomingElectionDatabase.getAll()
    val savedElections = savedElectionDatabase.getAll()

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            val electionResponse = api.getElections()
            upcomingElectionDatabase.insertAll(electionResponse.elections)
        }
    }
}