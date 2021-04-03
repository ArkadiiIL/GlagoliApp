package com.arkadii.glagoli.calendar

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.R

class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cellDayText: TextView = itemView.findViewById(R.id.cellDayText)
    var day: Day? = null
}