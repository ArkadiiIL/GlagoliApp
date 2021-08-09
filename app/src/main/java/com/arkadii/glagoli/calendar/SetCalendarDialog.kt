package com.arkadii.glagoli.calendar

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.arkadii.glagoli.R
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.record.SetAlarmDialog
import com.arkadii.glagoli.util.getTimePicker
import com.arkadii.glagoli.util.setAlarm
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.*

class SetCalendarDialog(private val context: Context,
                        private val fragmentManager: FragmentManager,
                        private val alarmViewModel: AlarmViewModel): CalendarDialog(context) {
    var setAlarmDialog: SetAlarmDialog? = null
    var currentRecordPath = ""

     override val listener: (CalendarViewHolder) -> Unit = { holder ->
        Log.d(TAG, "Set listener ${holder.day}")
        holder.cellDayText.setOnClickListener {
            Log.d(TAG, "click ${holder.day}")
            val picker = getTimePicker(context)
            picker.addOnPositiveButtonClickListener {
                pickerListener(holder, picker)
            }
                picker.show(fragmentManager, TAG)
        }
    }

    private fun pickerListener(holder: CalendarViewHolder, picker: MaterialTimePicker) {
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
                val dayOfWeek =
                    if(calendar.get(Calendar.DAY_OF_WEEK) == 1) 6
                    else calendar.get(Calendar.DAY_OF_WEEK) - 2
                val alarm
                        = createAlarm(
                    calendar.timeInMillis,
                    year,
                    month,
                    day,
                    dayOfWeek,
                    picker.hour,
                    picker.minute)

                Log.i(TAG, "Set new $alarm")
                setAlarm(alarm, context)

                saveAlarm(alarm)
                cancelDialog()
                val alarmDialog = setAlarmDialog
                if (alarmDialog != null) {
                    alarmDialog.closeDialog()
                } else error("SetAlarmDialog cannot be null!")
            } else {
                val toastText = context.getText(R.string.wrong_time)
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }
        } else error("Day must have fields filled in")
    }

    private fun createAlarm(timeInMillis: Long,
                            year: Int,
                            month: Int,
                            day: Int,
                            dayOfWeek: Int,
                            hour: Int,
                            minute: Int)
            = Alarm(0, timeInMillis, year, month, day, dayOfWeek, hour, minute, currentRecordPath)

    private fun saveAlarm(alarm: Alarm) {
        Log.i(TAG, "Save new Alarm $alarm")
        alarmViewModel.addAlarms(alarm)
    }
}