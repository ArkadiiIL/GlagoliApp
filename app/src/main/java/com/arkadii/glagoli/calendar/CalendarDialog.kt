package com.arkadii.glagoli.calendar

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.arkadii.glagoli.AlarmReceiver
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.databinding.CalendarDialogBinding
import com.arkadii.glagoli.record.RecordFragment
import com.arkadii.glagoli.record.SetAlarmDialog
import com.arkadii.glagoli.util.getAlarmManager
import com.arkadii.glagoli.util.getTimePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.*

class CalendarDialog(private val context: Context,
                     private val fragmentManager: FragmentManager,
                     private val alarmViewModel: AlarmViewModel) {
    private lateinit var calendarDialogBinding: CalendarDialogBinding
    private val metrics = Resources.getSystem().displayMetrics
    private lateinit var calendarController: CalendarController
    private lateinit var dialog: AlertDialog
    var setAlarmDialog: SetAlarmDialog? = null
    var currentRecordPath = ""

    fun showCalendarDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)

        initCalendarDialogBinding()
        initCalendarController()
        setListeners()

        builder.setView(calendarDialogBinding.root)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        Log.i(RecordFragment.TAG, "Show CalendarDialog")
        dialog.show()
    }

    private fun initCalendarDialogBinding() {
        Log.i(TAG, "Init CalendarDialogBinding")
        calendarDialogBinding = CalendarDialogBinding.inflate(
                LayoutInflater.from(context)
        )
    }

    private fun initCalendarController() {
        Log.i(TAG, "Init CalendarController")
        val calendarRealization = SimpleCalendarEngine()
        val adapter = SimpleCalendarAdapter(listener)

        calendarDialogBinding.rvDays.adapter = adapter
        calendarDialogBinding.rvDays.layoutManager = GridLayoutManager(context, 7 )
        calendarDialogBinding.rvDays.addItemDecoration(CalendarItemDecorator(metrics))

        calendarController = CalendarController(calendarRealization, adapter)
        calendarDialogBinding.twDate.text = calendarController.getDateText()
    }

    private fun setListeners() {
        calendarDialogBinding.nextMonthButton.setOnClickListener {
            calendarController.plusMonth()
            calendarDialogBinding.twDate.text = calendarController.getDateText()
        }
        calendarDialogBinding.previousMonthButton.setOnClickListener {
            calendarController.minusMonth()
            calendarDialogBinding.twDate.text = calendarController.getDateText()
        }
        calendarDialogBinding.cancelCalendarDialogBtn.setOnClickListener {
            cancelDialog()
        }
    }

    fun cancelDialog() {
        dialog.cancel()
    }

    private val listener: (CalendarViewHolder) -> Unit = { holder ->
        holder.cellDayText.setOnClickListener {
            val picker = getTimePicker(context)
            picker.addOnPositiveButtonClickListener {
               pickerListener(holder, picker)
            }
            picker.show(fragmentManager, SimpleCalendarAdapter.TAG)
        }
    }

    private fun pickerListener(holder: CalendarViewHolder, picker: MaterialTimePicker) {
        val calendar = Calendar.getInstance()
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
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val alarm
            = createAlarm(
                calendar.timeInMillis,
                year,
                month,
                day,
                dayOfWeek,
                picker.hour,
                picker.minute)

            setAlarm(alarm)
            saveAlarm(alarm)
            cancelDialog()
            val alarmDialog = setAlarmDialog
            if (alarmDialog != null) {
                alarmDialog.closeDialog()
            } else error("SetAlarmDialog cannot be null!")
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

    private fun setAlarm(alarm: Alarm) {
        Log.i(TAG, "Set new $alarm")
        val alarmManager = getAlarmManager(context)
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("path", alarm.recordPath)
        val id = System.currentTimeMillis().toInt()
        alarm.pendingIntentId = id
        val pendingIntent =
            PendingIntent.getBroadcast(context,
                id,
                intent,
                PendingIntent.FLAG_ONE_SHOT)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarm.alarmTime,
            pendingIntent
        )
    }

    companion object {
        const val TAG = "CalendarDialog"
    }
}