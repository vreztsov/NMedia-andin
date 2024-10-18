package ru.netology.nmedia.util

fun agoToText(
    timeInSecond: Int,
    inLastWeekTextDescription: String,
    inTwoDaysAgoTextDescription: String,
    inYesterdayTextDescription: String,
    inTodayTextDescription: String,
) = when (timeInSecond) {
    in 0..<60 * 60 * 24 -> inTodayTextDescription
    in 60 * 60 * 24..<60 * 60 * 24 * 2 -> inYesterdayTextDescription
    in 60 * 60 * 24 * 2..<60 * 60 * 24 * 7 -> inTwoDaysAgoTextDescription
    else -> inLastWeekTextDescription
}