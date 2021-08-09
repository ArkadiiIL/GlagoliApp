package com.arkadii.glagoli.calendar

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.arkadii.glagoli.R
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.util.cancelAlarm
import com.arkadii.glagoli.util.getTimePicker
import com.arkadii.glagoli.util.setAlarm
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.*

class EditCalendarDialog(private val context: Context,
                         private val fragmentManager: FragmentManager,
                         private val alarm: Alarm,
                         private val alarmViewModel: AlarmViewModel): CalendarDialog(context) {

    override val listener: (CalendarViewHolder) -> Unit = { holder ->
        holder.cellDayText.setOnClickListener {
            val picker = getTimePicker(context)
            setPickerAcceptListener(picker, holder)
            picker.show(fragmentManager, TAG)
        }
    }

    private fun setPickerAcceptListener(picker: MaterialTimePicker,
                                        holder: CalendarViewHolder) {
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            val currentTime = calendar.timeInMillis
            val year = holder.day?.year?.toInt()
            val month = holder.day?.month?.toInt()
            val day = holder.day?.day?.toInt()
            if ((year != null) && (month != null) && (day != null)) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendar.set(Calendar.MINUTE, picker.minute)
                calendar.set(Calendar.SECOND, 0)
                if(currentTime <= calendar.timeInMillis) {
                    if(alarm.isEnabled) {
                        Log.d(TAG, "Cancel $alarm")
                        cancelAlarm(alarm, context)
                        alarm.pendingIntentId = 0
                    }
                    alarm.pendingIntentId = System.currentTimeMillis().toInt()
                    alarm.alarmTime = calendar.timeInMillis
                    alarm.isEnabled = true
                    val dayOfWeek =
                        if(calendar.get(Calendar.DAY_OF_WEEK) == 1) 6
                        else calendar.get(Calendar.DAY_OF_WEEK) - 2
                    alarm.dayOfWeek = dayOfWeek
                    Log.d(TAG, "Set $alarm")
                    setAlarm(alarm, context)
                    Log.d(TAG, "Update $alarm")
                    alarmViewModel.updateAlarm(alarm)
                    cancelDialog()
                } else {
                    val toastText = context.getText(R.string.wrong_time)
                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                }
            } else error("Day must have fields filled in")
        }
    }

}