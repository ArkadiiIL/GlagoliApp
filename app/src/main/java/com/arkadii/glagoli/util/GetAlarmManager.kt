package com.arkadii.glagoli.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.arkadii.glagoli.AlarmActivity
import com.arkadii.glagoli.MainActivity
import com.arkadii.glagoli.data.Alarm

fun getAlarmManager(context: Context) = context.
getSystemService(Context.ALARM_SERVICE) as AlarmManager

fun setAlarm(alarm: Alarm, context: Context) {
    Log.d("setALARM", "alarm")
    val alarmManager = getAlarmManager(context)

    val id = System.currentTimeMillis().toInt()
    alarm.pendingIntentId = id

    val alarmClickInfo = AlarmManager.AlarmClockInfo(
        alarm.alarmTime,
        getAlarmInfoPendingIntent(context, alarm))

    alarmManager.setAlarmClock(
        alarmClickInfo,
        getAlarmActionPendingIntent(context, alarm))
}

fun getAlarmInfoPendingIntent(context: Context, alarm: Alarm): PendingIntent {
    val alarmInfo = Intent(context, MainActivity::class.java)
    alarmInfo.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    alarmInfo.putExtra("path", alarm.recordPath)
    return PendingIntent.getActivity(
        context,
        alarm.pendingIntentId,
        alarmInfo,
        PendingIntent.FLAG_UPDATE_CURRENT)
}

fun getAlarmActionPendingIntent(context: Context, alarm: Alarm): PendingIntent {
    val intent = Intent(context, AlarmActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("path", alarm.recordPath)
    return PendingIntent.getActivity(
        context,
        alarm.pendingIntentId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT)
}

fun cancelAlarm(alarm: Alarm, context: Context) {
    val alarmManager = getAlarmManager(context)
    val intent = Intent(context, AlarmActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        alarm.pendingIntentId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    alarmManager.cancel(pendingIntent)
}