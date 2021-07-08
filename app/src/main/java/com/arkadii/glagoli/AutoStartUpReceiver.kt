package com.arkadii.glagoli

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.arkadii.glagoli.data.AlarmDatabase
import com.arkadii.glagoli.data.AlarmRepository
import com.arkadii.glagoli.util.getAlarmManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AutoStartUpReceiver: BroadcastReceiver() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "AutoStartUpReceiver started")
        if(context != null) {
            init(context)
            setAlarms(context)
        }
    }

    private fun setAlarms(context: Context) {
        Log.i(TAG, "Set Alarms")
        CoroutineScope(Dispatchers.IO).launch {
            alarmRepository.getAllAlarms().forEach {
                Log.i(TAG, "Set $it")
                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(context, 0, intent, 0)
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    it.alarmTime,
                    pendingIntent
                )
            }
        }
    }

    private fun init(context: Context) {
        Log.i(TAG, "Init AlarmManager")
        alarmManager = getAlarmManager(context)
        Log.i(TAG, "Init AlarmRepository")
        alarmRepository = AlarmRepository(AlarmDatabase.getDatabase(context).alarmDAO())
    }

    companion object {
        const val TAG = "AutoStartUpReceiver"
    }
}