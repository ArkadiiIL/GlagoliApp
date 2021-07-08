package com.arkadii.glagoli.util

import com.arkadii.glagoli.R
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

fun getTimePicker(): MaterialTimePicker = MaterialTimePicker.Builder()
    .setTimeFormat(TimeFormat.CLOCK_24H)
    .setTitleText(R.string.select_time)
    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
    .build()