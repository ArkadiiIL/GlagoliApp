package com.arkadii.glagoli

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.arkadii.glagoli.databinding.ActivityAlarmBinding

class AlarmActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Alarm")
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        const val TAG = "AlarmActivity"
    }
}