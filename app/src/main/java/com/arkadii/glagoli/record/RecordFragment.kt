package com.arkadii.glagoli.record

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.arkadii.glagoli.MainActivity
import com.arkadii.glagoli.R
import com.arkadii.glagoli.databinding.FragmentRecordBinding
import com.arkadii.glagoli.databinding.SetDialogBinding
import com.arkadii.glagoli.extensions.toPx
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

class RecordFragment(private val viewPager: ViewPager2) : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in RecordFragment")
    private lateinit var mediaRecorderManager: MediaRecorderManager
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private lateinit var timerManager: TimerManager
    private var record = true
    private var buttonMove = ButtonMove.DEFAULT
    private var isButtonDown = false
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
            Log.v(TAG, "Init MediaRecorderManager")
            mediaRecorderManager = MediaRecorderManager(fActivity.applicationContext)
            Log.v(TAG, "Init MediaPlayerManager")
            mediaPlayerManager = MediaPlayerManager()

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
        Log.v(TAG, "Set onTouchListener in startButton")
        binding.buttonStart.setOnTouchListener { view, event ->
            val uncheckedParent = view.parent
            if(uncheckedParent is View) {
                val parentLocation = IntArray(2)
                uncheckedParent.getLocationOnScreen(parentLocation)

                if (event.action == MotionEvent.ACTION_DOWN && record && !isButtonDown) {

                    Log.i(TAG, "ACTION_DOWN with ButtonStart")
                    actionDown(view)
                } else if (event.action == MotionEvent.ACTION_UP && !record && isButtonDown) {

                    Log.i(TAG, "ACTION_UP with ButtonStart")
                    actionUp(view, parentLocation[1])
                } else if (event.action == MotionEvent.ACTION_MOVE && isButtonDown) {

                    Log.i(TAG, "ACTION_MOVE with ButtonStart")
                    actionMove(view, event, parentLocation[0], parentLocation[1])
                }
            }
            false
        }
    }

    private fun actionDown(view: View) {

        viewPager.isUserInputEnabled = false

        mediaRecorderManager.startRecording()
        timerManager.start(binding.textTime)
        binding.buttonStart.setImageResource(R.drawable.ic_action_stop)

        val location = IntArray(2)
        view.getLocationInWindow(location)
        buttonStartX = location[0].toFloat()
        buttonStartY = location[1].toFloat()
        binding.buttonStart.customSize = 65.toPx()

        binding.deleteIcon.visibility = View.VISIBLE
        binding.saveIcon.visibility = View.VISIBLE

        record = false

        isButtonDown = true
    }

    private fun actionUp(view: View, parentY: Int) {
        mediaRecorderManager.stopRecording()
        timerManager.stop(binding.textTime)
        binding.buttonStart.setImageResource(R.drawable.ic_action_play)
        binding.buttonStart.customSize = 56.toPx()
        record = true
        viewPager.isUserInputEnabled = true

        binding.buttonStart.animate().let { animator ->
            animator.x(abs(buttonStartX - (9.toPx()/2)))
            animator.y(abs(buttonStartY - parentY))
            animator.duration = 0
            animator.start()
        }

        binding.deleteIcon.visibility = View.GONE
        binding.saveIcon.visibility = View.GONE

        buttonMove = ButtonMove.DEFAULT
        isButtonDown = false
        showSetDialog()
    }

    private fun actionMove(view: View,
                           event: MotionEvent,
                           parentX: Int,
                           parentY: Int) {
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
                when {
                    event.rawX >= abs(95.toPx()) -> {
                        if(event.rawX < abs(buttonStartX + 33.toPx())) {

                            val targetX = abs(event.rawX - 28.toPx() - parentX)
                            val targetY = abs(buttonStartY - parentY)
                            animate(binding.buttonStart, targetX, targetY)
                        } else buttonMove = ButtonMove.DEFAULT
                    }
                    event.rawX < abs(95.toPx()) -> {
                        isButtonDown = false
                        actionUp(view, parentY)
                        buttonMove = ButtonMove.DEFAULT
                    }
                }
            }
            ButtonMove.TO_UP -> {
                when {
                    event.rawY <= (buttonStartY - 75.toPx())-> {
                        isButtonDown = false
                        actionUp(view, parentY)
                        buttonMove = ButtonMove.DEFAULT
                    }
                    event.rawY < (buttonStartY + 33.toPx()) -> {
                        val targetX = abs(buttonStartX - (9.toPx()/2))
                        val targetY = abs(event.rawY - 28.toPx() - parentY)
                        animate(binding.buttonStart, targetX, targetY)
                    }
                    else -> buttonMove = ButtonMove.DEFAULT
                }
            }
        }
    }

    private fun animate(button: FloatingActionButton,
                        x: Float,
                        y: Float,
                        duration: Long = 0) {
            button.animate().let { animator ->
                animator.y(y)
                animator.x(x)
                animator.duration = duration
                animator.start()
            }
        Log.i(TAG, "StartButton animation move " +
                "to x = $x,  y = $y, duration = $duration")
    }

    private fun showSetDialog() {
        val builder = AlertDialog.Builder(this.requireContext())
        val setDialogBinding = SetDialogBinding.inflate(
                LayoutInflater.from(this.requireContext())
        )
        setDialogBinding.audioNameText.text = "Hello World"
        setDialogBinding.playAudioBtn.setOnClickListener {
            mediaPlayerManager.initMediaPlayer(
                        AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build())
            mediaPlayerManager.play(mediaRecorderManager.currentRecord)
        }

        builder.setView(setDialogBinding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        Log.i(TAG, "Show set dialog")
        dialog.show()
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
        mediaPlayerManager.closeMediaPlayer()
        timerManager.stop(binding.textTime)
    }

    companion object {
        const val TAG = "RecordFragmentCHECKTAG"
    }
}