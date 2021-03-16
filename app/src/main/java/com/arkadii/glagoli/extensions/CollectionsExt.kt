package com.arkadii.glagoli.extensions

fun IntArray.containsOnly(num: Int) = any { it == num }