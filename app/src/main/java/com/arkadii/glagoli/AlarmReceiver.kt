package com.arkadii.glagoli

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null) {
            val scheduledIntent = Intent(context, AlarmActivity::class.java)
            scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(scheduledIntent)
        }
    }
}