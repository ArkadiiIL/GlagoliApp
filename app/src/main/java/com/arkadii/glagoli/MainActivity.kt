package com.arkadii.glagoli

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.arkadii.glagoli.databinding.ActivityMainScreenBinding
import com.arkadii.glagoli.extensions.containsOnly
import com.arkadii.glagoli.extensions.isPermissionGranted
import com.arkadii.glagoli.extensions.requestPermission
import com.arkadii.glagoli.extensions.shouldShowRequestPermissionRationaleExt
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityMainScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        Log.i(TAG, "Start MainActivity")

        Log.i(TAG, "Init ActivityMainBinding")
        binding = ActivityMainScreenBinding.inflate(layoutInflater)

        checkPermission()
    }

    private fun checkPermission() {
        Log.i(TAG, "Checking RECORD_AUDIO permission")

        if (!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "RECORD_AUDIO permission has not been granted")
            requestRecordAudioPermission()
        } else {
            Log.i(TAG, "RECORD_AUDIO has already been granted")

        }
    }

    private fun requestRecordAudioPermission() {
        Log.i(TAG, "Requesting RECORD_AUDIO permission")
        if(shouldShowRequestPermissionRationaleExt(Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "Displaying rationale for RECORD_AUDIO permission")

            Snackbar.make(binding.mainLayout,
                    R.string.permission_record_audio_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) {
                        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO)
                    }
                .show()

        } else {
            Log.i(TAG, "Request RECORD_AUDIO permission directly")
            requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        Log.i(TAG, "RequestPermissionResult")

        if(requestCode == RECORD_AUDIO) {
            Log.i(TAG, "Received response for RECORD_AUDIO permission request")

            if (grantResults.containsOnly(PackageManager.PERMISSION_GRANTED)) {
                Log.i(TAG, "RECORD_AUDIO permission has been granted")

                Snackbar.make(binding.mainLayout,
                    R.string.permission_available_record_audio,
                    Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                Log.i(TAG, "RECORD_AUDIO permission were not granted")

                Snackbar.make(binding.mainLayout,
                    R.string.permission_not_granted,
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun startRecord(view: View) {}

    companion object {
        const val TAG = "MainActivityTAG"
        const val RECORD_AUDIO = 1
    }
}
