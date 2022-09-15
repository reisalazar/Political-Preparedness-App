package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.udacity.project4.base.BaseFragment
import java.util.*

class DetailFragment : BaseFragment() {

    // Add Constant for Location request
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var enableLocationSettingLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var binding: FragmentRepresentativeBinding

    // Declare ViewModel
    override val viewModel: RepresentativesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_representative,
                container,
                false
            )

        //: Establish bindings
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Define and assign Representative adapter
        val adapter = RepresentativeListAdapter()
        binding.rvRepresentatives.adapter = adapter
        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            adapter.submitList(representatives)
        }

        // Populate Representative adapter


        // Establish button listeners for field and location search
        binding.btnSearch.setOnClickListener {
            hideKeyboard()
            viewModel.onSearchButtonClick()
        }

        registerLocationPermissionsCallback()
        registerEnableLocationCallback()
        binding.btnLocation.setOnClickListener { requestLocationPermissions() }

        return binding.root
    }

    // Handle location permission result to get location on permission granted
    private fun registerLocationPermissionsCallback() {

        requestLocationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (isGranted) {
                checkDeviceLocationSettingsAndGetLocation()
            } else {
                viewModel.showSnackBarInt.value = R.string.failed_location_permited
            }
        }
    }

    private fun registerEnableLocationCallback() {
        enableLocationSettingLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK)
                getLocation()
            else {
                viewModel.showSnackBarInt.value = R.string.failed_location_permited
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {

        if (isPermissionGranted()) {
            checkDeviceLocationSettingsAndGetLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // Get location from LocationServices
        // The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult.let {

                    val address = geoCodeLocation(it.lastLocation)
                    viewModel.refreshByCurrentLocation(address)

                    fusedLocationProviderClient.removeLocationUpdates(this)
                }
            }
        }
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        Looper.myLooper()?.let {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback,
                it
            )
        }
    }

    private fun checkDeviceLocationSettingsAndGetLocation(resolve: Boolean = true) {

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {

                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    enableLocationSettingLauncher.launch(intentSenderRequest)

                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            } else {
                viewModel.showSnackBarInt.value = R.string.failed_location_permited
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
            return geocoder?.getFromLocation(location.latitude, location.longitude, 1)
                ?.map { address ->
                    Address(
                        address.thoroughfare,
                        address.subThoroughfare,
                        address.locality,
                        address.adminArea,
                        address.postalCode
                    )
                }
                ?.first()!!
        }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}