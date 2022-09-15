package com.example.android.politicalpreparedness.network.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voter_info_table")
data class VoterInfo(
    @PrimaryKey val id: Int,
    val stateName: String,
    val votingLocationUrl: String?,
    val ballotInformationUrl: String?)
