package ru.netology.nmedia.util

fun agoToText(
    timeInSecond: Int,
    inLastWeekTextDescription: String,
    inYesterdayTextDescription: String,
    inTodayTextDescription: String,
) = when (timeInSecond) {
    in 0..<60 * 60 * 24 -> inTodayTextDescription
    in 60 * 60 * 24..<60 * 60 * 24 * 2 -> inYesterdayTextDescription
    else -> inLastWeekTextDescription
}