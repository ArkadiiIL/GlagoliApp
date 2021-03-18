package com.arkadii.glagoli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.arkadii.glagoli.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper
    private lateinit var mediaRecorderManager: MediaRecorderManager
    var record = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Start MainActivity")

        init()
        permissionHelper.checkPermission()
    }

    private fun init() {
        Log.i(TAG, "Init function")

        Log.v(TAG, "Init ActivityMainBinding")
        binding = ActivityMainBinding.inflate(layoutInflater)

        Log.v(TAG, "Set Content View")
        setContentView(binding.root)

        Log.v(TAG, "Init PermissionHelper")
        permissionHelper = PermissionHelper(this, binding.root)

        Log.v(TAG, "Init MediaRecorderManager")
        mediaRecorderManager = MediaRecorderManager(this)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        Log.i(TAG, "RequestPermissionResult")

        if(requestCode == RECORD_AUDIO) {
            permissionHelper.checkPermissionResult(
                requestCode,
                permissions,
                grantResults)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun startRecord(view: View) {
        record = if(record) {
            mediaRecorderManager.startRecording()
            false
        } else {
            mediaRecorderManager.stopRecording()
            true
        }
    }

    companion object {
        const val TAG = "MainActivityTAG"
        const val RECORD_AUDIO = 1
    }
}
