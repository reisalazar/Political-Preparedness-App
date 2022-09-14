package com.example.android.politicalpreparedness.util

import com.example.android.politicalpreparedness.data.repository.LocalRepository
import com.example.android.politicalpreparedness.data.repository.NetworkRepository
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService

object ObjectServices {
    internal lateinit var database: ElectionDatabase

    internal lateinit var networkService: CivicsApiService

    private val electionDao: Lazy<ElectionDao> = lazy {
        return@lazy database.electionDao
    }

    val electionLocalRepository: Lazy<LocalRepository> = lazy {
        return@lazy LocalRepository(electionDao)
    }

    val electionNetworkRepository: Lazy<NetworkRepository> = lazy {
        return@lazy NetworkRepository(networkService)
    }
}