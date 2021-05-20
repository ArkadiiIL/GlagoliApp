package com.arkadii.glagoli.record

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.arkadii.glagoli.MainActivity
import com.arkadii.glagoli.R
import com.arkadii.glagoli.databinding.FragmentRecordBinding
import com.arkadii.glagoli.extensions.toPx
import kotlin.math.abs

class RecordFragment(private val viewPager: ViewPager2) : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in RecordFragment")
    private lateinit var mediaRecorderManager: MediaRecorderManager
    private lateinit var timerManager: TimerManager
    private var record = true
    private var buttonMove = ButtonMove.DEFAULT
    private var buttonStartX = 0f
    private var buttonStartY = 0f

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
        binding.buttonStart.setOnTouchListener { view, event ->
            val uncheckedParent = view.parent
            if(uncheckedParent is View) {
                val parentLocation = IntArray(2)
                uncheckedParent.getLocationOnScreen(parentLocation)

                if (event.action == MotionEvent.ACTION_DOWN && record) {

                    Log.i(TAG, "ACTION_DOWN with ButtonStart")

                    viewPager.isUserInputEnabled = false

                    mediaRecorderManager.startRecording()
                    timerManager.start(binding.textTime)
                    binding.buttonStart.setImageResource(R.drawable.ic_action_stop)

                    val location = IntArray(2)
                    view.getLocationInWindow(location)
                    buttonStartX = location[0].toFloat()
                    buttonStartY = location[1].toFloat()
                    binding.buttonStart.customSize = 65.toPx()
                    Log.i(TAG, "ButtonStart animation start with x = $buttonStartX y = $buttonStartY")

                    binding.deleteIcon.visibility = View.VISIBLE
                    binding.saveIcon.visibility = View.VISIBLE

                    record = false

                } else if (event.action == MotionEvent.ACTION_UP && !record) {

                    Log.i(TAG, "ACTION_UP with ButtonStart")

                    mediaRecorderManager.stopRecording()
                    timerManager.stop(binding.textTime)
                    binding.buttonStart.setImageResource(R.drawable.ic_action_play)
                    binding.buttonStart.customSize = 56.toPx()
                    record = true
                    viewPager.isUserInputEnabled = true

                    binding.buttonStart.animate().let { animator ->
                        animator.x(abs(buttonStartX - (9.toPx()/2)))
                        animator.y(abs(buttonStartY - parentLocation[1]))
                        animator.duration = 0
                        animator.start()
                    }

                    binding.deleteIcon.visibility = View.GONE
                    binding.saveIcon.visibility = View.GONE

                    buttonMove = ButtonMove.DEFAULT

                } else if (event.action == MotionEvent.ACTION_MOVE) {

                    Log.i(TAG, "ACTION_MOVE with ButtonStart")

                    when(buttonMove) {
                        ButtonMove.DEFAULT -> {
                            val shiftX = buttonStartX - event.rawX
                            val shiftY = buttonStartY - event.rawY
                            Log.i(TAG, "shiftX  = $shiftX, shiftY = $shiftY" )

                            if(shiftX > 0.1 && shiftX > shiftY) buttonMove = ButtonMove.TO_LEFT
                            else if(shiftY > 0.1) buttonMove = ButtonMove.TO_UP
                            Log.i(TAG, "buttonMOVE = $buttonMove")
                        }
                        ButtonMove.TO_LEFT -> {
                            if(event.rawX < abs(buttonStartX + 33.toPx())) {
                                binding.buttonStart.animate().let { animator ->
                                    val targetX = abs(event.rawX - 28.toPx() - parentLocation[0])
                                    animator.x(targetX)
                                    animator.duration = 0
                                    animator.start()
                                    Log.i(TAG, "ButtonStart animation move to x = $targetX")
                                }
                            } else buttonMove = ButtonMove.DEFAULT
                        }
                        ButtonMove.TO_UP -> {
                            if(event.rawY < (buttonStartY + 33.toPx())) {
                                binding.buttonStart.animate().let { animator ->
                                    val targetY = abs(event.rawY - 28.toPx() - parentLocation[1])
                                    animator.y(targetY)
                                    animator.duration = 0
                                    animator.start()
                                    Log.i(TAG, "ButtonStart animation move to y = $targetY")
                                }
                            } else buttonMove = ButtonMove.DEFAULT
                        }
                    }
                }
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