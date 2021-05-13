package com.arkadii.glagoli.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.arkadii.glagoli.R
import com.arkadii.glagoli.databinding.FragmentCalendarBinding

class CalendarFragment: Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in CalendarFragment")
    private lateinit var calendarManager: CalendarManager

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
            calendarManager = CalendarManager(fActivity, binding.rvDays)
            binding.twDate.text = calendarManager.getDateText()
        } else {
            Log.w(TAG, "fActivity is null")
        }
        binding.nextMonthButton.setOnClickListener {
            calendarManager.plusMonth()
            binding.twDate.text = calendarManager.getDateText()
        }
        binding.previousMonthButton.setOnClickListener {
            calendarManager.minusMonth()
            binding.twDate.text = calendarManager.getDateText()
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