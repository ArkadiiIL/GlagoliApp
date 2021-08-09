package com.arkadii.glagoli.alarmmenu

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.AlarmReceiver
import com.arkadii.glagoli.R
import com.arkadii.glagoli.calendar.EditCalendarDialog
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.record.MediaPlayerManager
import com.arkadii.glagoli.util.cancelAlarm
import com.arkadii.glagoli.util.setAlarm
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AlarmMenuAdapter(private val context: Context,
                       private val alarmViewModel: AlarmViewModel,
                       private val fragmentManager: FragmentManager,
                       private val mediaPlayerManager: MediaPlayerManager
                       ): RecyclerView.Adapter<AlarmMenuViewHolder>() {

    private var data = emptyList<Alarm>()
    private var playMap = ConcurrentHashMap<String, AlarmMenuViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmMenuViewHolder {
        Log.v(TAG, "OnCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        return AlarmMenuViewHolder(inflater.inflate(
            R.layout.alarm_menu_item, parent, false
        ))
    }

    override fun onBindViewHolder(holder: AlarmMenuViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder")
        val alarm = data[position]
        setAlarmData(alarm, holder)
        setAlarmListeners(alarm, holder)
    }

    private fun setAlarmData(alarm: Alarm, holder: AlarmMenuViewHolder) {
        Log.d(TAG, "Set alarm data for $alarm")
        holder.tvDay.text = getDayOfWeek(alarm.dayOfWeek)
        val calendar = getCalendar(alarm)
        holder.tvTime.text = getTime(calendar)
        holder.tvFullDate.text = getFullDate(calendar)
        holder.tvRecordName.text = File(alarm.recordPath).name
        holder.tvRecordName.isSelected = true
        holder.swEnable.isChecked = alarm.isEnabled
    }

    private fun getDayOfWeek(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            0 -> context.resources.getString(R.string.monday_EEE)
            1 -> context.resources.getString(R.string.tuesday_EEE)
            2 -> context.resources.getString(R.string.wednesday_EEE)
            3 -> context.resources.getString(R.string.thursday_EEE)
            4 -> context.resources.getString(R.string.friday_EEE)
            5 -> context.resources.getString(R.string.saturday_EEE)
            6 -> context.resources.getString(R.string.sunday_EEE)
            else -> ""
        }
    }

    private fun getCalendar(alarm: Alarm): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = alarm.alarmTime
        return calendar
    }

    private fun getTime(calendar: Calendar): String {
        val format = if (DateFormat.is24HourFormat(context))
            SimpleDateFormat("hh:mm", Locale.getDefault())
        else SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun getFullDate(calendar: Calendar) :String {
        val format = SimpleDateFormat("dd:MM:yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun setAlarmListeners(alarm: Alarm, holder: AlarmMenuViewHolder) {
        setDeleteListener(alarm, holder)
        setSwitchListener(alarm, holder)
        setEditListener(alarm, holder)
        setPlayAudioListener(alarm, holder)
    }

    private fun setPlayAudioListener(alarm: Alarm, holder: AlarmMenuViewHolder) {
        holder.playAudioAlarmMenuBtn.setOnClickListener {
            if(!playMap.containsKey(alarm.recordPath)) playMusic(alarm, holder)
            else stopMusic(alarm, holder)
        }
    }

    private fun stopMusic(alarm: Alarm, holder: AlarmMenuViewHolder) {
        mediaPlayerManager.stop()
        playMap.remove(alarm.recordPath)
        holder.playAudioAlarmMenuBtn.setImageResource(R.drawable.ic_button_play_alarm_menu)
    }

    private fun playMusic(alarm: Alarm, holder: AlarmMenuViewHolder) {
        playMap.forEach {
            it.value.playAudioAlarmMenuBtn.setImageResource(R.drawable.ic_button_play_alarm_menu)
        }
        playMap.clear()
        playMap[alarm.recordPath] = holder
        mediaPlayerManager.initMediaPlayer(AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build(), alarm.recordPath)
        holder.playAudioAlarmMenuBtn.setImageResource(R.drawable.ic_button_stop_alarm_menu)
        mediaPlayerManager.setCompletionListener { stopMusic(alarm,holder) }
        mediaPlayerManager.play()
    }

    private fun setEditListener(alarm: Alarm, holder: AlarmMenuViewHolder) {
        holder.btnEdit.setOnClickListener {
            editAlarm(alarm)
        }
    }

    private fun editAlarm(alarm: Alarm) {
        val editCalendarDialog = EditCalendarDialog(context, fragmentManager, alarm, alarmViewModel)
        Log.i(TAG, "Show edit dialog")
        editCalendarDialog.showCalendarDialog()
    }

    private fun setSwitchListener(alarm: Alarm, holder: AlarmMenuViewHolder) {
        holder.swEnable.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                if(System.currentTimeMillis() < alarm.alarmTime) {
                    if(alarm.pendingIntentId == 0) {
                        Log.i(TAG, "Turn on $alarm")
                        alarm.isEnabled = true
                        Log.d(TAG, "Set $alarm")
                        setAlarm(alarm, context)
                        alarmViewModel.updateAlarm(alarm)
                    }
                } else {
                    buttonView.isChecked = false
                    editAlarm(alarm)
                }
            } else {
                alarm.isEnabled = false
                Log.d(TAG, "Cancel $alarm")
                cancelAlarm(alarm, context)
                alarm.pendingIntentId = 0
                alarmViewModel.updateAlarm(alarm)
            }
        }
    }

    private fun setDeleteListener(alarm: Alarm, holder: AlarmMenuViewHolder) {
        holder.btnDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.delete_alert)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    if(alarm.isEnabled) {
                        cancelAlarm(alarm, context)
                    }
                    deleteRecord(alarm)
                    alarmViewModel.deleteAlarm(alarm)
                    dialog.cancel()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }
    }

    private fun deleteRecord(alarm: Alarm) {
        val file = File(alarm.recordPath)
        file.delete()
    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<Alarm>) {
        this.data = data
        notifyDataSetChanged()
    }


    companion object {
        const val TAG = "AlarmMenuAdapter"
    }

}