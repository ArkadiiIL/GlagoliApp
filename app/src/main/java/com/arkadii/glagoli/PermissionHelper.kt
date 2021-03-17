package com.arkadii.glagoli

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arkadii.glagoli.extensions.containsOnly
import com.arkadii.glagoli.extensions.isPermissionGranted
import com.arkadii.glagoli.extensions.requestPermission
import com.arkadii.glagoli.extensions.shouldShowRequestPermissionRationaleExt
import com.google.android.material.snackbar.Snackbar

class PermissionHelper(private val context: AppCompatActivity,
                       private val view: View) {

    fun checkPermission() {
        Log.i(TAG, "Checking RECORD_AUDIO permission")

        if (!context.isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "RECORD_AUDIO permission has not been granted")
            requestRecordAudioPermission()
        } else {
            Log.i(MainActivity.TAG, "RECORD_AUDIO has already been granted")

        }
    }

    fun checkPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "Received response for RECORD_AUDIO permission request")

        if (grantResults.containsOnly(PackageManager.PERMISSION_GRANTED)) {
            Log.i(TAG, "RECORD_AUDIO permission has been granted")

            Snackbar.make(view,
                R.string.permission_available_record_audio,
                Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Log.i(TAG, "RECORD_AUDIO permission were not granted")
            Log.v(TAG, "Show alert Dialog")
            AlertDialog.Builder(context)
                .setMessage(R.string.permission_not_granted)
                .setCancelable(false)
                .setPositiveButton(R.string.to_settings) { _, _ ->
                    Log.i(TAG, "Redirect to app setting and close app")
                    context.finish()
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }
                .setNegativeButton(R.string.close_app) { _, _ ->
                    Log.i(TAG, "Close app")
                    context.finish()
                }
                .show()
        }

    }

    private fun requestRecordAudioPermission() {
        Log.i(TAG, "Requesting RECORD_AUDIO permission")
        if(context.shouldShowRequestPermissionRationaleExt(Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "Displaying rationale for RECORD_AUDIO permission")

            Snackbar.make(view,
                R.string.permission_record_audio_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .show()

            context.requestPermission(Manifest.permission.RECORD_AUDIO, MainActivity.RECORD_AUDIO)

        } else {
            Log.i(TAG, "Request RECORD_AUDIO permission directly")
            context.requestPermission(Manifest.permission.RECORD_AUDIO, MainActivity.RECORD_AUDIO)
        }
    }

    companion object {
        const val TAG = "PermissionHelperTAG"
    }
}