package com.arkadii.glagoli.calendar

import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CalendarItemDecorator(private val metrics: DisplayMetrics): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = ((metrics.heightPixels/10) * 0.166666666).toInt()
        outRect.right = 0

    }

}