package com.arkadii.glagoli.calendar

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.AlarmActivity
import com.arkadii.glagoli.AlarmReceiver
import com.arkadii.glagoli.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class SimpleCalendarAdapter(private val context: Context,
                            private val fragmentManager: FragmentManager):
        RecyclerView.Adapter<CalendarViewHolder>(),
        CalendarAdapter {
    private val days = mutableListOf<Day>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        Log.v(TAG, "OnCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        return CalendarViewHolder(inflater.inflate(
                R.layout.calendar_cell, parent, false))
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder")
        holder.day = days[position]
        holder.cellDayText.text = holder.day?.day
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
                calendar.set(Calendar.HOUR_OF_DAY, picker.hour)
                calendar.set(Calendar.MINUTE, picker.minute)
                calendar.set(Calendar.SECOND, 0)
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                )
            }
            picker.show(fragmentManager, TAG)
        }

    }

    override fun getItemCount(): Int = days.size

   override fun updateCalendar(days: MutableList<Day>) {
        Log.v(TAG, "updateCalendar")
        this.days.clear()
        this.days.addAll(days)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "SimpleCalendarAdapterCHECKTAG"
    }
}