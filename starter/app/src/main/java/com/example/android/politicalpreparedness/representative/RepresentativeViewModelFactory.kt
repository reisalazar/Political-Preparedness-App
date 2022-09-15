package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.repository.NetworkRepository

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(
    private val networkDataRepository: Lazy<NetworkRepository>,
    application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = RepresentativeViewModel(
        networkDataRepository
    ) as T
}