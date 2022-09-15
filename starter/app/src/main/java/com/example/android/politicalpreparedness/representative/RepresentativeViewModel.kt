package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.repository.RepresentativesRepository
import com.example.android.politicalpreparedness.network.CivicsApiInstance
import com.example.android.politicalpreparedness.network.models.Address
import com.udacity.project4.base.BaseViewModel
import kotlinx.coroutines.launch

class RepresentativesViewModel(app: Application) : BaseViewModel(app) {
    private val repository = RepresentativesRepository(CivicsApiInstance)

    // Establish live data for representatives and address
    val representatives = repository.representatives

    private val _addressList = MutableLiveData<Address>()
    val addressList: LiveData<Address>
        get() = _addressList

    private val _statesList = MutableLiveData<List<String>>()
    val statesList: LiveData<List<String>>
        get() = _statesList

    init {
        _addressList.value = Address("", "", "", "Mississippi", "")
        _statesList.value = app.resources.getStringArray(R.array.states).toList()
    }


    val stateSelected = MutableLiveData<Int>()

    // Create function to fetch representatives from API from a provided address
    private fun refreshRepresentatives() {
        viewModelScope.launch {
            try {
                addressList.value!!.state = getSelectedState(stateSelected.value!!)
                val addressStr = addressList.value!!.toFormattedString()
                repository.refreshRepresentatives(addressStr)

            } catch (exception: Exception) {
                showSnackBarInt.postValue(R.string.failed_address)
            }
        }
    }

    fun searchButtonClick() {
        refreshRepresentatives()
    }

    // Create function get address from geo location
    fun refreshCurrentLocation(address: Address) {

        val stateIndex = _statesList.value?.indexOf(address.state)
        if (stateIndex != null && stateIndex >= 0) {
            stateSelected.value = stateIndex!!
            _addressList.value = address
            refreshRepresentatives()

        } else {
            showSnackBarInt.value = R.string.failed_representatives
        }
    }

    private fun getSelectedState(stateIndex: Int): String {
        return statesList.value!!.toList()[stateIndex]
    }
    // Create function to get address from individual fields

}
