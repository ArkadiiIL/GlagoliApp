package com.arkadii.glagoli.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.arkadii.glagoli.MainActivity
import com.arkadii.glagoli.R
import com.arkadii.glagoli.calendar.SetCalendarDialog
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.databinding.FragmentRecordBinding
import com.arkadii.glagoli.extensions.toPx

class RecordFragment(private val viewPager: ViewPager2) : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in $TAG")
    private lateinit var mediaRecorderManager: MediaRecorderManager
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private lateinit var timerManager: TimerManager
    private lateinit var setAlarmDialog: SetAlarmDialog
    private lateinit var calendarDialog: SetCalendarDialog
    @Volatile
    private var holdButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "Start init function in onCreate")
        init()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.v(TAG, "Init binding and setListeners in CreateView")
       _binding = FragmentRecordBinding.inflate(inflater, container, false)
        Log.i(TAG, "Set RecordButtonListener")
        binding.startBtn.leftBorder = 95
        binding.startBtn.upBorder = 95
         setRecordButtonListeners()

        return binding.root
    }

    private fun init() {
        val fActivity = activity
        if(fActivity != null) {
            Log.i(TAG, "Init MediaRecorderManager")
            mediaRecorderManager = MediaRecorderManager(fActivity.applicationContext)
            Log.i(TAG, "Init MediaPlayerManager")
            mediaPlayerManager = MediaPlayerManager()
            Log.i(TAG, "Init CalendarDialog")
            val alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
            calendarDialog = SetCalendarDialog(requireContext(),
                childFragmentManager,
                alarmViewModel)
            Log.i(TAG, "Init SetDialog")
            setAlarmDialog = SetAlarmDialog(requireContext())

            calendarDialog.setAlarmDialog = setAlarmDialog
            setAlarmDialog.calendarDialog = calendarDialog

            if(fActivity is AppCompatActivity) {
                Log.i(MainActivity.TAG, "Init TimeManager")
                timerManager = TimerManager(fActivity)
            } else {
                Log.e(TAG, "Context is not AppCompatActivity")
            }
        } else {
            Log.e(TAG, "Context is Null")
        }
    }

    private fun setRecordButtonListeners() {
        Log.i(TAG, "SetRecordButtonListeners")
        setActionDownListener()
        setActionUpListener()
        setLeftListener()
        setUpListener()

    }

    private fun setActionDownListener() {
        binding.startBtn.actionDownListener = { button, _ ->
            Log.i(TAG, "ActionDownListener")
            if(holdButton) {
                unlockHoldButton(button)
            } else {
                startRecord(button)
            }
        }
    }

    private fun startRecord(button: MoveFloatingActionButton) {
        Log.i(TAG, "StartRecord listener")
        viewPager.isUserInputEnabled = false

        mediaRecorderManager.startRecording()
        timerManager.start(binding.textTime)
        button.setImageResource(R.drawable.ic_action_stop)

        button.customSize = 65.toPx()

        binding.deleteIcon.visibility = View.VISIBLE
        binding.saveIcon.visibility = View.VISIBLE
    }

    private fun unlockHoldButton(button: MoveFloatingActionButton) {
        Log.i(TAG, "UnlockHold listener")
        stopButton(button)
        setAlarmDialog.showSetAlertDialog()
        holdButton = false
    }

    private fun setActionUpListener() {
        binding.startBtn.actionUpListener = {button, _ ->
            Log.i(TAG, "ActionUpListener")
            stopButton(button)

            setAlarmDialog.showSetAlertDialog()
        }
    }

    private fun setLeftListener() {
        binding.startBtn.leftListener = {button, _ ->
            Log.i(TAG, "LeftListener")
            stopButton(button)

            setAlarmDialog.deleteCurrentRecord()
        }
    }

    private fun stopButton(button: MoveFloatingActionButton) {
        binding.deleteIcon.visibility = View.GONE
        binding.saveIcon.visibility = View.GONE

        mediaRecorderManager.stopRecording()
        timerManager.stop(binding.textTime)

        button.customSize = 56.toPx()
        button.setImageResource(R.drawable.ic_action_play)
        button.returnToStartLocation()

        setAlarmDialog.setCurrentRecordPath(mediaRecorderManager.currentRecordPath)

        viewPager.isUserInputEnabled = true
    }

    private fun setUpListener() {
        binding.startBtn.upListener = {button, _ ->
            holdButton = true
            button.setImageResource(R.drawable.ic_action_stop_hold)
            button.returnToStartLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView in $TAG")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Stop record and timer in onDestroy")
        mediaRecorderManager.stopRecording()
        mediaPlayerManager.closeMediaPlayer()
        timerManager.stop(binding.textTime)
    }


    companion object {
        const val TAG = "RecordFragment"
    }
}