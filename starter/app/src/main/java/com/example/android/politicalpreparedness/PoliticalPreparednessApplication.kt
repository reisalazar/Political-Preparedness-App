package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.util.ObjectServices

class PoliticalPreparednessApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectServices.electionDatabase = ElectionDatabase.getInstance(this)
        ObjectServices.civicsApiService = CivicsApi.retrofitService
    }
}