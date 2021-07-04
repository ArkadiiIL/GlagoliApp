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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.arkadii.glagoli.AlarmReceiver
import com.arkadii.glagoli.R
import com.arkadii.glagoli.data.Alarm
import com.arkadii.glagoli.data.AlarmDatabase
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.databinding.CalendarDialogBinding
import com.arkadii.glagoli.record.RecordFragment
import com.arkadii.glagoli.record.SetAlarmDialog
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class CalendarDialog(private val context: Context,
                     private val fragmentManager: FragmentManager) {
    private lateinit var calendarDialogBinding: CalendarDialogBinding
    private val metrics = Resources.getSystem().displayMetrics
    private lateinit var calendarController: CalendarController
    private lateinit var dialog: AlertDialog
    var setAlarmDialog: SetAlarmDialog? = null
    private var currentRecordPath = ""

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
        val adapter = SimpleCalendarAdapter(context, listener)

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
            val picker =
                    MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_24H)
                            .setTitleText(R.string.select_time)
                            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                            .build()
            picker.addOnPositiveButtonClickListener {
                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(context, 0, intent, 0)
                val calendar = Calendar.getInstance()
                val year = holder.day?.year?.toInt()
                val month = holder.day?.month?.toInt()
                val day = holder.day?.month?.toInt()
                if ((year != null) && (month != null) && (day != null)) {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                    calendar.set(Calendar.MINUTE, picker.minute)
                    calendar.set(Calendar.SECOND, 0)
                    saveAlarm(calendar.timeInMillis, year, month, day, picker.hour, picker.minute)
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    val alarmDialog = setAlarmDialog
                    if (alarmDialog != null) {
                        alarmDialog.closeDialog()
                    } else error("SetAlarmDialog cannot be null!")
                } else error("Day must have fields filled in")
            }
            picker.show(fragmentManager, SimpleCalendarAdapter.TAG)
        }
    }

    private fun saveAlarm(alarmTime: Long,
                          year: Int,
                          month: Int,
                          day: Int,
                          hour: Int,
                          minute: Int ) {
        val alarmViewModel = ViewModelProvider(context.)
    }

    companion object {
        const val TAG = "CalendarDialog"
    }
}