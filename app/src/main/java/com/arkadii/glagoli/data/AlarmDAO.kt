package com.arkadii.glagoli.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDAO {
    @Query("SELECT * FROM alarm_table")
    fun getAll(): LiveData<List<Alarm>>

    @Insert
    fun insert(alarms: Array<out Alarm>)

    @Update
    fun update(alarm: Alarm)

    @Delete
    fun delete(alarm: Alarm)
}