package com.arkadii.glagoli

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.arkadii.glagoli.extensions.isPermissionGranted


class MainActivity : AppCompatActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Start MainActivity")

        setContentView(R.layout.activity_main)

        Log.i(TAG, "Checking RECORD_AUDIO permission")

        if(!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "RECORD_AUDIO permission has not been granted")

            requestRecordAudioPermission()
        } else {
            Log.i(TAG, "RECORD_AUDIO has already been granted")
        }


    }

    private fun requestRecordAudioPermission() {
        Log.i(TAG, "Requesting RECORD_AUDIO permission")

    }

    fun startRecord(view: View) {}

    companion object {
        const val TAG = "MainActivity"
    }
}
