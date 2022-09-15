package com.example.android.politicalpreparedness.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.politicalpreparedness.network.models.Election

@Database(entities = [Election::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ActiveElectionDatabase: RoomDatabase() {

    protected abstract val dao: ElectionDao

    companion object {

        @Volatile
        private var INSTANCE: ActiveElectionDatabase? = null

        fun getInstance(context: Context): ActiveElectionDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ActiveElectionDatabase::class.java,
                        "active_election_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    suspend fun insertAll(elections: List<Election>) = dao.insertAll(elections)
    fun getAll() = dao.getAllElections()
}