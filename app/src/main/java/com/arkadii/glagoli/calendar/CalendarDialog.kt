package com.arkadii.glagoli.calendar

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import com.arkadii.glagoli.databinding.CalendarDialogBinding
import com.arkadii.glagoli.record.RecordFragment

abstract class CalendarDialog(private val context: Context) {
    private lateinit var calendarDialogBinding: CalendarDialogBinding
    private val metrics = Resources.getSystem().displayMetrics
    private lateinit var calendarController: CalendarController
    private lateinit var dialog: AlertDialog


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

    abstract val listener: (CalendarViewHolder) -> Unit

    companion object {
        const val TAG = "CalendarDialog"
    }
}