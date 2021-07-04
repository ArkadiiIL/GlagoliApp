package com.arkadii.glagoli.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Alarm::class],
    version = 1,
    exportSchema = false)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun alarmDAO(): AlarmDAO

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase {
            val temInstance = INSTANCE

            if(temInstance != null) return temInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}