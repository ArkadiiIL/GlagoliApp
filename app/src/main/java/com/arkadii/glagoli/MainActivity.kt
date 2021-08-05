package com.arkadii.glagoli

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arkadii.glagoli.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(),  ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper
    private var switch = true

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

        Log.v(TAG, "Init pager adapter")
        binding.viewPager.adapter = ViewPageAdapter(this, binding.viewPager)
        binding.viewPager.currentItem = 0
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

    companion object {
        const val TAG = "MainActivityCHECKTAG"
        const val RECORD_AUDIO = 1
    }

    fun testSlideClick(view: View) {
        if(switch) {
            binding.viewPager.currentItem = 1
            switch = false
        } else {
            binding.viewPager.currentItem = 0
            switch = true
        }
    }
}
