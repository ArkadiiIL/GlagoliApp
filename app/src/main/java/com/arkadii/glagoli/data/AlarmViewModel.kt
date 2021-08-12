package com.arkadii.glagoli.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AlarmRepository
    val getAllAlarms: LiveData<List<Alarm>>

    init {
        val alarmDAO = AlarmDatabase.getDatabase(application).alarmDAO()
        repository = AlarmRepository(alarmDAO)
        getAllAlarms = repository.getAllLiveData()
    }

    fun addAlarms(vararg alarms: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addAlarms(alarms)
        }
    }

    suspend fun getAlarmByPath(path: String): Alarm = repository.getAlarmByPath(path)


    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlarm(alarm)
        }
    }


}