package com.bruno.movies

import java.text.SimpleDateFormat
import java.util.*

val dateFormat = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())

fun format(date: Calendar) = dateFormat.format(date.time)!!

fun defaultReleaseDate(): String{
    return format(Calendar.getInstance())
}