package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.Status
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val networkRepository: Lazy<NetworkRepository>,
    val app: Application
) : ViewModel() {

    // Establish live data for representatives and address
    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MediatorLiveData<String>()
    val zip = MutableLiveData<String>()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _showSnackBar = MutableLiveData<Int>()
    val showSnackBar: LiveData<Int>
        get() = _showSnackBar

    val selectedItem = MutableLiveData<Int>()

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    init {
        state.addSource(selectedItem) {
            state.value = app.resources.getStringArray(R.array.states)[it]
        }
    }


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
                _showSnackBar.value = R.string.snack_failed_representatives
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
            _showSnackBar.value = R.string.snack_failed_address
            return false
        }
        if (city.value.isNullOrBlank()) {
            _showSnackBar.value = R.string.snack_failed_city

            return false
        }
        if (zip.value.isNullOrBlank()) {
            _showSnackBar.value = R.string.snack_failed_zip
            return false
        }
        return true
    }


}
