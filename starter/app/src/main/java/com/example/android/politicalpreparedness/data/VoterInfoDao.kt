package com.example.android.politicalpreparedness.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.VoterInfo

@Dao
interface VoterInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(voterInfo: VoterInfo)

    @Query("SELECT * FROM voter_info_table WHERE id = :id")
    suspend fun get(id: Int) : VoterInfo?
}