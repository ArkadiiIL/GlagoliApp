package com.arkadii.glagoli.util

import android.content.Context
import android.text.format.DateFormat
import com.arkadii.glagoli.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

fun getTimePicker(context: Context): MaterialTimePicker = MaterialTimePicker.Builder()
    .setTimeFormat(
            if(DateFormat.is24HourFormat(context)) TimeFormat.CLOCK_24H
            else TimeFormat.CLOCK_12H)
    .setTitleText(R.string.select_time)
    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
    .build()