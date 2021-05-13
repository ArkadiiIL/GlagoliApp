package com.arkadii.glagoli.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.R

class CalendarAdapter(private var days: MutableList<Day>): RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        Log.v(TAG, "OnCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val layoutParams = parent.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(inflater.inflate(
                R.layout.calendar_cell, parent, false))
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder")
        holder.day = days[position]
        holder.cellDayText.text = holder.day?.day
    }

    override fun getItemCount(): Int = days.size

    fun updateCalendar(days: MutableList<Day>) {
        Log.v(TAG, "updateCalendar")
        this.days.clear()
        this.days.addAll(days)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "CalendarAdapterCHECKTAG"
    }
}