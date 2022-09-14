package com.example.android.politicalpreparedness.util

import com.example.android.politicalpreparedness.data.repository.LocalRepository
import com.example.android.politicalpreparedness.data.repository.NetworkRepository
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService

object ObjectServices {
    private lateinit var electionDatabase: ElectionDatabase

    private lateinit var civicsApiService: CivicsApiService

    private val electionDao: Lazy<ElectionDao> = lazy {
        return@lazy electionDatabase.electionDao
    }

    val localRepository: Lazy<LocalRepository> = lazy {
        return@lazy LocalRepository(electionDao)
    }

    val networkRepository: Lazy<NetworkRepository> = lazy {
        return@lazy NetworkRepository(civicsApiService)
    }
}