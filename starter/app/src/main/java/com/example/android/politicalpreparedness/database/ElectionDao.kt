package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.viewbinding.BuildConfig
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    // Add insert query
    @Insert
    suspend fun insert(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(elections : List<Election>)

    // Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAllElections(): LiveData<List<Election>>

    // Add select single election query
    @Query("SELECT * FROM election_table WHERE id=:id")
    fun get(id:Int): Election

    // Add delete query
    @Delete
    suspend fun delete(id:Int)

    // Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()

}