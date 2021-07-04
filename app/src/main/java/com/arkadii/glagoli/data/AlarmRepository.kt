package com.arkadii.glagoli.data

class AlarmRepository(private val alarmDAO: AlarmDAO) {

    fun getAllAlarms() = alarmDAO.getAll()

    suspend fun addAlarms(alarms: Array<out Alarm>) = alarmDAO.insert(alarms)

    suspend fun updateAlarm(alarm: Alarm) = alarmDAO.update(alarm)

    suspend fun deleteAlarm(alarm: Alarm) = alarmDAO.delete(alarm)
}