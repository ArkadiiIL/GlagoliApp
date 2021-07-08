package com.arkadii.glagoli.util

import android.app.AlarmManager
import android.content.Context

fun getAlarmManager(context: Context) = context.
getSystemService(Context.ALARM_SERVICE) as AlarmManager