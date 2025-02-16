package com.alaturing.umusicapp.common.utils

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

fun parseDuration(formattedDuration: String): Int {
    try {
        val parts = formattedDuration.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()
            return minutes * 60 + seconds
        }
    } catch (e: Exception) {
        // En caso de error, devolver 0
    }
    return 0
}