package com.arkadii.glagoli.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.arkadii.glagoli.AlarmReceiver
import com.arkadii.glagoli.data.Alarm

fun getAlarmManager(context: Context) = context.
getSystemService(Context.ALARM_SERVICE) as AlarmManager

fun setAlarm(alarm: Alarm, context: Context) {
    val alarmManager = getAlarmManager(context)
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("path", alarm.recordPath)
    val id = System.currentTimeMillis().toInt()
    alarm.pendingIntentId = id
    val pendingIntent =
        PendingIntent.getBroadcast(context,
            id,
            intent,
            PendingIntent.FLAG_ONE_SHOT)
    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        alarm.alarmTime,
        pendingIntent
    )
}

fun cancelAlarm(alarm: Alarm, context: Context) {
    val alarmManager = getAlarmManager(context)
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.pendingIntentId,
        intent,
        PendingIntent.FLAG_ONE_SHOT
    )
    alarmManager.cancel(pendingIntent)
}