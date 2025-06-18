package com.akuntansimandiri.app.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun convertToIsoDate(input: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())
        val date = LocalDate.parse(input, formatter)
        date.format(DateTimeFormatter.ISO_LOCAL_DATE) // hasil: "2025-05-04"
    } catch (e: Exception) {
        Logger.w("DateFormat", "Tanggal tidak valid: $input")
        ""
    }
}
