package com.example.android.politicalpreparedness.util

import com.example.android.politicalpreparedness.data.ElectionDao
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApiService

object ObjectServices {
    lateinit var electionDatabase: ElectionDatabase

    lateinit var civicsApiService: CivicsApiService

    private val electionDao: Lazy<ElectionDao> = lazy {
        return@lazy electionDatabase.electionDao
    }

    val localRepository: Lazy<LocalRepository> = lazy {
        return@lazy LocalRepository(electionDao)
    }

    var networkRepository: Lazy<NetworkRepository> = lazy {
        return@lazy NetworkRepository(civicsApiService)
    }
}