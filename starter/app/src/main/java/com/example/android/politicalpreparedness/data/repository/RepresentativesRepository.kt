package com.example.android.politicalpreparedness.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepresentativesRepository(private val api: CivicsApiService) {

    private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: LiveData<List<Representative>?>
        get() = _representatives

    suspend fun refreshRepresentatives(addressStr: String) {
        withContext(Dispatchers.IO) {
            _representatives.postValue(null)
            val representativeResponse = api.getAllRepresentatives(addressStr)

            val representativeList = representativeResponse.offices.flatMap { office ->
                office.getRepresentatives(representativeResponse.officials)
            }

            _representatives.postValue(representativeList as MutableList<Representative>?)
        }
    }
}