package com.arkadii.glagoli.record

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arkadii.glagoli.MainActivity
import com.arkadii.glagoli.R
import com.arkadii.glagoli.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in RecordFragment")
    private lateinit var mediaRecorderManager: MediaRecorderManager
    private lateinit var timerManager: TimerManager
    var record = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "start init function in onCreate")
        init()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.v(TAG, "init binding and setListeners in on CreateView")
       _binding = FragmentRecordBinding.inflate(inflater, container, false)

        setListeners()
        return binding.root
    }


    private fun init() {
        val fActivity = activity
        if(fActivity != null) {
            Log.v(MainActivity.TAG, "Init MediaRecorderManager")
            mediaRecorderManager = MediaRecorderManager(fActivity.applicationContext)

            if(fActivity is AppCompatActivity) {
                Log.v(MainActivity.TAG, "Init TimeManager")
                timerManager = TimerManager(fActivity)
            } else {
                Log.e(TAG, "Context is not AppCompatActivity")
            }
        } else {
            Log.e(TAG, "Context is Null")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        Log.v(MainActivity.TAG, "Set onTouchListener in startButton")
        binding.buttonStart.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN && record) {
                mediaRecorderManager.startRecording()
                timerManager.start(binding.textTime)
                binding.buttonStart.setImageResource(R.drawable.ic_action_stop)
                binding.buttonStart.customSize = 180
                record = false
            } else if(event.action == MotionEvent.ACTION_UP && !record) {
                mediaRecorderManager.stopRecording()
                timerManager.stop(binding.textTime)
                binding.buttonStart.setImageResource(R.drawable.ic_action_play)
                binding.buttonStart.customSize = 150
                record = true
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "on Destroy View")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Stop record and timer in onDestroy")
        mediaRecorderManager.stopRecording()
        record = true
        timerManager.stop(binding.textTime)
    }

    companion object {
        const val TAG = "RecordFragmentCHECKTAG"
    }



}