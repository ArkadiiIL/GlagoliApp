package com.arkadii.glagoli

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.arkadii.glagoli.data.Alarm

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "$TAG Start onReceive()")
        if(context != null) {
            val scheduledIntent = Intent(context, AlarmActivity::class.java)
            val path = intent?.getStringExtra("path")
            scheduledIntent.putExtra("path", path)
            scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.i(TAG, "Start new AlarmActivity")
            context.startActivity(scheduledIntent)
        }
    }
    companion object {
        const val TAG = "AlarmReceiver"
    }
}