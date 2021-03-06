package com.arkadii.glagoli.record

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.arkadii.glagoli.R
import com.arkadii.glagoli.calendar.SetCalendarDialog
import com.arkadii.glagoli.databinding.SetAlarmDialogBinding
import com.arkadii.glagoli.extensions.getRecordName
import com.arkadii.glagoli.extensions.setRecordFormat
import java.io.File

class SetAlarmDialog(private val context: Context) {
    private var currentRecordPath = ""
    private val mediaPlayerManager: MediaPlayerManager = MediaPlayerManager()
    private lateinit var setDialogBinding: SetAlarmDialogBinding
    private lateinit var dialog: AlertDialog
    private val path = "${context.externalCacheDir?.absolutePath}/"
    var calendarDialog: SetCalendarDialog? = null
    @Volatile
    private var isPlay = false

    fun showSetAlertDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)

        initSetDialogBinding()
        setListeners()

        val file = File(currentRecordPath)

        initMediaPlayer()
        mediaPlayerManager.setCompletionListener {
            Log.d(TAG, "MediaPlayerManagerListener")
            Log.d(TAG, "play = $isPlay")
            if(isPlay) {
                switchPlay()
                secondClickOnPlayAudioButton()
            }
        }

        setDialogBinding.audioNameText.text = file.name.getRecordName()
        setDialogBinding.audioNameText.isSelected = true
        Log.i(RecordFragment.TAG, file.name.getRecordName())
        setDialogBinding.playAudioBtn.setOnClickListener {
            Log.d(TAG, "First click play = $isPlay")

            switchPlay()
            Log.d(TAG, "After switch play = $isPlay")

            if(isPlay) {
                firstClickOnPlayAudioButton()
                playMediaPlayer()
            } else {
                secondClickOnPlayAudioButton()
                stopMediaPlayer()
            }
        }
        builder.setView(setDialogBinding.root)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        Log.i(RecordFragment.TAG, "Show SetAlertDialog")
        dialog.show()
    }

    private fun initSetDialogBinding() {
        Log.i(TAG, "Init SetAlarmDialogBinding")
        setDialogBinding = SetAlarmDialogBinding.inflate(
                LayoutInflater.from(context)
        )
    }

    private fun setListeners() {
        setRenameButtonListener()
        setCancelRenameButtonListener()
        setAcceptRenameButtonListener()
        setEditNameListener()
        setDeleteButtonListener()
        setSetButtonListener()
    }

    private fun firstClickOnPlayAudioButton() {
        val stopDraw = ContextCompat.getDrawable(context, R.drawable.ic_button_stop)
        setDialogBinding.playAudioBtn
                .setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        stopDraw,
                        null)
        setDialogBinding.playAudioBtn.apply {
            setPaddingRelative(
                    paddingStart,
                    paddingTop,
                    paddingEnd + 25,
                    paddingBottom
            )
        }
    }

    private fun secondClickOnPlayAudioButton() {
        val playDraw = ContextCompat.getDrawable(context, R.drawable.ic_button_play)
        setDialogBinding.playAudioBtn
                .setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        playDraw,
                        null)
        setDialogBinding.playAudioBtn.apply {
            setPaddingRelative(
                    paddingStart,
                    paddingTop,
                    paddingEnd - 25,
                    paddingBottom
            )
        }
    }

    private fun initMediaPlayer() {
        mediaPlayerManager.initMediaPlayer(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build(), currentRecordPath)
    }

    private fun playMediaPlayer() {
        mediaPlayerManager.play()
    }

    private fun stopMediaPlayer() {
        mediaPlayerManager.stop()
    }

    private fun closeMediaPlayer() {
        mediaPlayerManager.closeMediaPlayer()
    }

    private fun setRenameButtonListener() {
        Log.i(RecordFragment.TAG, "Set onClickListener in RenameButton")
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
                setDialogBinding.editName.error = context.getString(R.string.cannot_be_empty)
                false
            }
            !text.matches(Regex("^[^*&%\\s]+\$")) -> {
                Log.i(TAG, "Text not matching")
                setDialogBinding.editName.error = context.getString(R.string.incorrect_filename)
                false
            }
            text.length > 50 -> {
                Log.i(TAG, "Text for rename is greater than 50 symbols")
                setDialogBinding.editName.error = context.getString(R.string.length_is_greater)
                false
            }
            else -> {
                val file = File(currentRecordPath)
                val pathToRename = "${path}${text.setRecordFormat()}"
                val fileToRename = File(pathToRename)
                Log.d(TAG, "File path = ${file.absolutePath}")
                Log.d(TAG, "RenameFile path = ${fileToRename.absolutePath}")
                if(fileToRename.exists()) {
                    Log.i(RecordFragment.TAG, "File exists")
                    setDialogBinding.editName.error = context.getString(R.string.file_exist)
                    false
                } else {
                    Log.i(RecordFragment.TAG, "Rename file")
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
            closeDialog()
        }
    }

    private fun setSetButtonListener() {
        setDialogBinding.setBtn.setOnClickListener {
            Log.i(RecordFragment.TAG, "Show CalendarDialog")
            val calendar = calendarDialog
            if(calendar != null) {
                calendar.currentRecordPath = currentRecordPath
                calendar.showCalendarDialog()
            } else error("CalendarDialog cannot be null")
        }
    }

    fun closeDialog() {
        Log.i(RecordFragment.TAG, "CLose SetAlertDialog")
        dialog.cancel()
        closeMediaPlayer()
        isPlay = false
    }

     fun deleteCurrentRecord(): Boolean {
        val file = File(currentRecordPath)
        return if(file.exists()) {
            Log.i(RecordFragment.TAG, "Delete current file record $currentRecordPath")
            file.delete()
            currentRecordPath = ""
            true
        } else {
            Log.w(RecordFragment.TAG, "The current file has been deleted $currentRecordPath" )
            false
        }
    }

    fun setCurrentRecordPath(url: String) {
        currentRecordPath = url
    }
    @Synchronized
    private fun switchPlay() {
        isPlay = !isPlay
    }

    companion object {
        const val TAG = "SetAlarmDialog"
    }
}