package com.arkadii.glagoli.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.R

class CalendarAdapter(private val days: List<Day>): RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layoutParams = parent.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return CalendarViewHolder(inflater.inflate(
                R.layout.calendar_cell, parent, false))
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.day = days[position]
        Log.d(TAG, "position == $position, day == ${days[position]}")
        holder.cellDayText.text = holder.day?.day
    }

    override fun getItemCount(): Int = days.size

    companion object {
        const val TAG = "CalendarAdapterCHECKTAG"
    }
}