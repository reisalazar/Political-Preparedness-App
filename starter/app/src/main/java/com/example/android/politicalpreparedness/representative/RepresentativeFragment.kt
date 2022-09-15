package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.util.ObjectServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        // Add Constant for Location request
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private lateinit var binding: FragmentRepresentativeBinding


    // Declare ViewModel
    private val viewModel by viewModels<RepresentativeViewModel> {
        RepresentativeViewModelFactory(
            ObjectServices.networkRepository, requireActivity().application
        )
    }

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Define and assign Representative adapter

        // Populate Representative adapter
        binding.rvRepresentatives.adapter = RepresentativeListAdapter()


        // Establish button listeners for field and location search
        binding.btnSearch.setOnClickListener {
            viewModel.fetchRepresentatives()
            hideKeyboard()
        }

        binding.btnLocation.setOnClickListener {
            hideKeyboard()
            if (checkLocationPermissions()) getLocation()
        }
        viewModel.showSnackBar.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE).show()
        }
        return binding.root
    }

    // Handle location permission result to get location on permission granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Snackbar.make(
                    binding.root, "You need to give Location Permission", Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            // Request Location permissions
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        // Get location from LocationServices
        // The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        val locationManager =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, true)
        if (provider != null) {
            val location: Location? = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                val address = geoCodeLocation(location)
                viewModel.getAddressLocation(address)
            } else {
                Snackbar.make(binding.root, "Failed to get device Location", Snackbar.LENGTH_LONG)
                    .show()
            }
        } else {
            Snackbar.make(binding.root, "You need enable Location", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}