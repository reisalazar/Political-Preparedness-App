package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.UpcomingElectionDatabase
import com.example.android.politicalpreparedness.data.SavedElectionDatabase
import com.example.android.politicalpreparedness.data.repository.ElectionsRepository
import com.example.android.politicalpreparedness.network.CivicsApiInstance
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
     import com.udacity.project4.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

// Construct ViewModel and provide election datasource
class ElectionsViewModel(app: Application) : BaseViewModel(app) {

    private val repository = ElectionsRepository(
        UpcomingElectionDatabase.getInstance(app),
        SavedElectionDatabase.getInstance(app), CivicsApiInstance
    )

    // Create live data val for upcoming elections
    val upcomingElections = repository.upcomingElections

    // Create live data val for saved elections
    val savedElections = repository.savedElections

    private val _mockElections = MutableLiveData<List<Election>>()

    // Create functions to navigate to saved or upcoming election voter info
    private fun refreshElections() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.refreshElections()

            } catch (exception: Exception) {
                showSnackBarInt.postValue(R.string.no_connection)
            }
        }
    }

    private fun mockElections() {
        val mockElections = mutableListOf<Election>()
        var count = 1
        if (count <= 5) {
            val data = Election(
                count,
                "name:$count",
                Date(),
                Division("id", "US", "state")
            )
            mockElections.add(data)
            ++count
        }
        _mockElections.postValue(mockElections)
    }

    // Create val and functions to populate live data for upcoming elections from the API
    // and saved elections from local database
    private val mockData: Boolean = false

    init {
        if (mockData) {
            mockElections()
        } else {
            refreshElections()
        }
    }
}