package com.codenode.budgetlens.common

import java.text.SimpleDateFormat
import java.util.*

class Utilities {
    companion object {

        fun convertDateToFormat(milliSeconds: Long, dateFormat: String?): String? {
            val formatter = SimpleDateFormat(dateFormat, Locale.CANADA)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }
}