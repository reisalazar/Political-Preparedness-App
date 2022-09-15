package com.example.android.politicalpreparedness.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.politicalpreparedness.network.models.Election

@Database(entities = [Election::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SavedElectionDatabase: RoomDatabase() {

    protected abstract val dao: ElectionDao

    companion object {

        @Volatile
        private var INSTANCE: SavedElectionDatabase? = null

        fun getInstance(context: Context): SavedElectionDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SavedElectionDatabase::class.java,
                        "saved_election_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    fun getAll() = dao.getAllElections()
    suspend fun get(id: Int) = dao.get(id)
    suspend fun insert(election: Election) = dao.insert(election)
    suspend fun delete(election: Election) = dao.delete(election)
}