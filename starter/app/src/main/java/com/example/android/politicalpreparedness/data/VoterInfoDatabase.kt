package com.example.android.politicalpreparedness.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.politicalpreparedness.network.models.VoterInfo

@Database(entities = [VoterInfo::class], version = 1, exportSchema = false)
abstract class VoterInfoDatabase: RoomDatabase() {

    abstract val dao: VoterInfoDao

    companion object {

        @Volatile
        private var INSTANCE: VoterInfoDatabase? = null

        fun getInstance(context: Context): VoterInfoDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        VoterInfoDatabase::class.java,
                        "voter_info_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

    suspend fun insert(voterInfo: VoterInfo) = dao.insert(voterInfo)
    suspend fun get(id: Int) = dao.get(id)
}