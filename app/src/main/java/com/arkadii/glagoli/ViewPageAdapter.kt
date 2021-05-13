package com.arkadii.glagoli

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.arkadii.glagoli.calendar.CalendarFragment
import com.arkadii.glagoli.record.RecordFragment

class ViewPageAdapter(activity: AppCompatActivity,
                      private val viewPager: ViewPager2): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) RecordFragment(viewPager)
        else CalendarFragment()
    }

}