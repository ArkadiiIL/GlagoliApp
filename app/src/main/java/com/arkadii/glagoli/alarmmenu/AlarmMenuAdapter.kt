package com.arkadii.glagoli.alarmmenu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.R
import com.arkadii.glagoli.data.Alarm

class AlarmMenuAdapter(val context: Context): RecyclerView.Adapter<AlarmMenuViewHolder>() {

    private var data = emptyList<Alarm>()

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
        holder.tvDay.text = getDayOfWeek(alarm.dayOfWeek)
        holder.tvFullDate.text = "${alarm.day}.${alarm.month}.${alarm.year}"


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

    private fun setAlarmListeners(alarm: Alarm, holder: AlarmMenuViewHolder) {

    }


    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<Alarm>) {
        this.data = data
    }

    companion object {
        const val TAG = "AlarmMenuAdapter"
    }

}