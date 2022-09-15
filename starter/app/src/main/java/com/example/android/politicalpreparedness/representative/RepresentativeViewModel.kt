package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.repository.NetworkRepository
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.Status
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val networkRepository: Lazy<NetworkRepository>,
    ) : ViewModel() {

    // Establish live data for representatives and address
    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _showSnackBar = MutableLiveData<String>()
    val showSnackBar: LiveData<String>
        get() = _showSnackBar

    val selectedItem = MutableLiveData<Int>()

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status


    // Create function to fetch representatives from API from a provided address
    fun fetchRepresentatives() {
        viewModelScope.launch {
            Status.LOADING
            kotlin.runCatching {
                val result =
                    networkRepository.value.getAllRepresentativesAsync(getCurrentAddress()).await()
                _representatives.value = result.offices.flatMap {
                    it.getRepresentatives(result.officials)
                }
                Status.SUCCESS
            }.onFailure {
                _showSnackBar.value = "Failed to fetch Representatives"
                Status.ERROR("Error on loading Representatives")
            }
        }
    }


    // Create function get address from geo location
    fun getAddressLocation(address: Address) {
        addressLine1.value = address.line1
        addressLine2.value = address.line2
        city.value = address.city
        state.value = address.state
        zip.value = address.zip
    }

    private fun getCurrentAddress() = Address(
        addressLine1.value.toString(),
        addressLine2.value.toString(),
        city.value.toString(),
        state.value.toString(),
        zip.value.toString()
    )


    // Create function to get address from individual fields
    private fun validateEnteredData(): Boolean {
        if (addressLine1.value.isNullOrBlank()) {
            _showSnackBar.value = "You need to set a Address"
            return false
        }
        if (city.value.isNullOrBlank()) {
            _showSnackBar.value = "You need to set a city"

            return false
        }
        if (zip.value.isNullOrBlank()) {
            _showSnackBar.value = "You need to set a Zip Code"
            return false
        }
        return true
    }


}
