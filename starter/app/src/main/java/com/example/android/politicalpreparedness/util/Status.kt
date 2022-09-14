package com.example.android.politicalpreparedness.util

sealed class Status {
    object LOADING: Status()
    class ERROR(val message: String?): Status()
    object SUCCESS: Status()
}