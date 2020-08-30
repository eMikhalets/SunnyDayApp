package com.emikhalets.sunnydayapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun citiesDao(): CitiesDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun implement(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, AppDatabase::class.java, "weather.db"
                    ).build()
                }
            }
            return INSTANCE
        }

        fun get() = INSTANCE!!
    }
}