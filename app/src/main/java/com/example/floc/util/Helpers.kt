package com.example.floc.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}