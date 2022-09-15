package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.repository.RepresentativesRepository
import com.example.android.politicalpreparedness.network.CivicsApiInstance
import com.example.android.politicalpreparedness.network.models.Address
import com.udacity.project4.base.BaseViewModel
import kotlinx.coroutines.launch

class RepresentativesViewModel(app: Application): BaseViewModel(app) {
    private val repository = RepresentativesRepository(CivicsApiInstance)

    // Establish live data for representatives and address
    val representatives = repository.representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _states = MutableLiveData<List<String>>()
    val states: LiveData<List<String>>
        get() = _states



    init {
        _address.value = Address("", "","","Mississippi","")
        _states.value = app.resources.getStringArray(R.array.states).toList()
    }


    val selectedStateIndex = MutableLiveData<Int>()

    // Create function to fetch representatives from API from a provided address
    private fun refreshRepresentatives() {
        viewModelScope.launch {
            try {
                address.value!!.state = getSelectedState(selectedStateIndex.value!!)
                val addressStr = address.value!!.toFormattedString()
                repository.refreshRepresentatives(addressStr)

            } catch (exception: Exception) {
                showSnackBarInt.postValue(R.string.failed_address)
            }
        }
    }
    fun onSearchButtonClick() {
        refreshRepresentatives()
    }


    // Create function get address from geo location
    fun refreshByCurrentLocation(address: Address) {

        val stateIndex = _states.value?.indexOf(address.state)
        if (stateIndex != null && stateIndex >= 0) {
            selectedStateIndex.value = stateIndex
            _address.value = address
            refreshRepresentatives()

        } else {
            showSnackBarInt.value = R.string.failed_representatives
        }
    }
    private fun getSelectedState(stateIndex: Int) : String {
        return states.value!!.toList()[stateIndex]
    }
    // Create function to get address from individual fields

}
