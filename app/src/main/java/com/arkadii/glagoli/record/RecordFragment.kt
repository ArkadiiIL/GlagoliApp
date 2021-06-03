package com.arkadii.glagoli.record

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.arkadii.glagoli.MainActivity
import com.arkadii.glagoli.R
import com.arkadii.glagoli.databinding.FragmentRecordBinding
import com.arkadii.glagoli.databinding.SetDialogBinding
import com.arkadii.glagoli.extensions.getRecordName
import com.arkadii.glagoli.extensions.setRecordFormat
import com.arkadii.glagoli.extensions.toPx
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import kotlin.math.abs
import kotlin.math.truncate

class RecordFragment(private val viewPager: ViewPager2) : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in RecordFragment")
    private lateinit var mediaRecorderManager: MediaRecorderManager
    private lateinit var mediaPlayerManager: MediaPlayerManager
    private lateinit var timerManager: TimerManager
    private lateinit var setDialogBinding: SetDialogBinding
    private var record = true
    private var buttonMove = ButtonMove.DEFAULT
    private var isButtonDown = false
    private var buttonStartX = 0f
    private var buttonStartY = 0f
    private lateinit var path: String
    private var currentRecordPath = ""
    private lateinit var dialog: AlertDialog

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
            Log.i(TAG, "Init MediaRecorderManager")
            mediaRecorderManager = MediaRecorderManager(fActivity.applicationContext)
            Log.i(TAG, "Init MediaPlayerManager")
            mediaPlayerManager = MediaPlayerManager()
            Log.i(TAG, "Init SetDialogBinding")
            setDialogBinding = SetDialogBinding.inflate(
                LayoutInflater.from(this.requireContext())
            )

            Log.i(TAG, "Init Path")
            path = "${context?.externalCacheDir?.absolutePath}/"



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

    private fun setListeners() {
        setButtonStartListener()
        setRenameButtonListener()
        setCancelRenameButtonListener()
        setAcceptRenameButtonListener()
        setEditNameListener()
        setDeleteButtonListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonStartListener() {
        Log.i(TAG, "Set onTouchListener in startButton")
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
                    actionUp(parentLocation[1], true)
                } else if (event.action == MotionEvent.ACTION_MOVE && isButtonDown) {

                    Log.i(TAG, "ACTION_MOVE with ButtonStart")
                    actionMove(view, event, parentLocation[0], parentLocation[1])
                }
            }
            false
        }
    }

    private fun setRenameButtonListener() {
        Log.i(TAG, "Set onClickListener in RenameButton")
        setDialogBinding.renameBtn.setOnClickListener{
            setDialogBinding.editName.visibility = View.VISIBLE
            setDialogBinding.cancelRenameBtn.visibility = View.VISIBLE
            setDialogBinding.acceptRenameBtn.visibility = View.VISIBLE
        }
    }

    private fun setCancelRenameButtonListener() {
        setDialogBinding.cancelRenameBtn.setOnClickListener {
            closeEditName()
        }
    }

    private fun setAcceptRenameButtonListener() {
        setDialogBinding.acceptRenameBtn.setOnClickListener {
            acceptRenameButton()
        }
    }
    private fun setEditNameListener() {
        setDialogBinding.editName.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                acceptRenameButton()
            }
             false
        }
    }

    private fun acceptRenameButton(): Boolean {
        val text = setDialogBinding.editName.text.toString()
        return when {
            text == "" -> {
                Log.i(TAG, "Text for rename record is empty")
                setDialogBinding.editName.error = getString(R.string.cannot_be_empty)
                false
            }
            text.length > 50 -> {
                Log.i(TAG, "Text for rename is greater than 50 symbols")
                setDialogBinding.editName.error = getString(R.string.length_is_greater)
                false
            }
            else -> {
                val file = File(currentRecordPath)
                val pathToRename = "$path${text.setRecordFormat()}"
                val fileToRename = File(pathToRename)
                Log.d(TAG, "File path = ${file.absolutePath}")
                Log.d(TAG, "RenameFile path = ${fileToRename.absolutePath}")
                if(fileToRename.exists()) {
                    Log.i(TAG, "File exists")
                    setDialogBinding.editName.error = getString(R.string.file_exist)
                    false
                } else {
                    Log.i(TAG, "Rename file")
                    file.renameTo(fileToRename)
                    currentRecordPath = pathToRename
                    setDialogBinding.audioNameText.text = text
                    closeEditName()
                    true
                }
            }
        }
    }

    private fun closeEditName() {
        setDialogBinding.editName.text.clear()
        setDialogBinding.editName.visibility = View.GONE
        setDialogBinding.cancelRenameBtn.visibility = View.GONE
        setDialogBinding.acceptRenameBtn.visibility = View.GONE
    }

    private fun setDeleteButtonListener() {
        setDialogBinding.deleteBtn.setOnClickListener {
            deleteCurrentRecord()
            Log.i(TAG, "CLose SetDialog")
            dialog.cancel()
        }
    }

    private fun deleteCurrentRecord(): Boolean {
        val file = File(currentRecordPath)
        return if(file.exists()) {
            Log.i(TAG, "Delete current file record $currentRecordPath")
            file.delete()
            currentRecordPath = ""
            true
        } else {
            Log.w(TAG, "The current file has been deleted $currentRecordPath" )
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

    private fun actionUp(parentY: Int, callSetDialog: Boolean) {
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
        currentRecordPath = mediaRecorderManager.currentRecordPath
        if(callSetDialog) {
            showSetDialog()
        }
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
                        actionUp(parentY, false)
                        deleteCurrentRecord()
                        buttonMove = ButtonMove.DEFAULT
                    }
                }
            }
            ButtonMove.TO_UP -> {
                when {
                    event.rawY <= (buttonStartY - 75.toPx())-> {
                        isButtonDown = false
                        actionUp(parentY, false)
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
        builder.setCancelable(false)

        val file = File(currentRecordPath)

        setDialogBinding.audioNameText.text = file.name.getRecordName()
        Log.i(TAG, file.name.getRecordName())
        setDialogBinding.playAudioBtn.setOnClickListener {
            mediaPlayerManager.initMediaPlayer(
                        AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build())
            mediaPlayerManager.play(currentRecordPath)
        }

        builder.setView(setDialogBinding.root)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        Log.i(TAG, "Show SetDialog")
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