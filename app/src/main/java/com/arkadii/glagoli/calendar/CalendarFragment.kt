package com.arkadii.glagoli.calendar

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.arkadii.glagoli.databinding.FragmentCalendarBinding

class CalendarFragment: Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in CalendarFragment")
    private val metrics = Resources.getSystem().displayMetrics
    private lateinit var calendar: CalendarController

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        Log.i(TAG, "Call init fun in CalendarFragment")
        val fActivity = activity
        if(fActivity != null) {
            val calendarRealization = SimpleCalendarEngine()
            val adapter = SimpleCalendarAdapter()

            binding.rvDays.adapter = adapter
            binding.rvDays.layoutManager = GridLayoutManager(context, 7 )
            binding.rvDays.addItemDecoration(CalendarItemDecorator(metrics))

            calendar = CalendarController(calendarRealization, adapter)
            binding.twDate.text = calendar.getDateText()
        } else {
            Log.w(TAG, "fActivity is null")
        }
        binding.nextMonthButton.setOnClickListener {
            calendar.plusMonth()
            binding.twDate.text = calendar.getDateText()
        }
        binding.previousMonthButton.setOnClickListener {
            calendar.minusMonth()
            binding.twDate.text = calendar.getDateText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v(TAG, "DestroyView CalendarFragment")
        _binding = null
    }

    companion object {
        const val TAG = "CalendarFragmentCHECKTAG"
    }
}