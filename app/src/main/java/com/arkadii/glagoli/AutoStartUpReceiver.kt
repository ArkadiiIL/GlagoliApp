package com.arkadii.glagoli

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AutoStartUpReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "AutoStartUpReceiver started")
    }
    companion object {
        const val TAG = "AutoStartUpReceiver"
    }
}