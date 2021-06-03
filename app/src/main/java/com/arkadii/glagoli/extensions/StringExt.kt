package com.arkadii.glagoli.extensions

fun String.getRecordName(): String = this.split(".")[0]
fun String.setRecordFormat(): String = "$this.m4a"