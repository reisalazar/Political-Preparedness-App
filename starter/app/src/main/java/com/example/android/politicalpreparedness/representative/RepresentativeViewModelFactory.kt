package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(
    private val networkDataRepository: Lazy<NetworkRepository>,
    private val application: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = RepresentativeViewModel(
        networkDataRepository, application
    ) as T
}