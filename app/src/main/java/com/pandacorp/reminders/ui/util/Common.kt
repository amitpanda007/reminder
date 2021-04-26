package com.pandacorp.reminders.ui.util

import android.util.Log
import com.pandacorp.reminders.LOG_TAG

class Common {

    companion object {
        fun convertToAMPM(time: String): String {
            // Time format in input should be HH:MM
            if(time.contains("AM") || time.contains("PM")) {
                return time
            }
            val time = time.split(":")
            var hour = time[0].toInt()
            var min = time[1]
            val timeFormat: String

            Log.i(LOG_TAG, "TIME $hour:$min")
            if(hour == 12) {
                timeFormat = "PM"
            } else if(hour > 12) {
                hour -= 12
                timeFormat = "PM"
            } else if (hour == 0) {
                hour = 12
                timeFormat = "AM"
            } else {
                timeFormat = "AM"
            }

            if (min == "0" || min == "00") {
                min = ""
            } else if(min.toInt() < 10) {
                min = ":0$min"
            } else {
                min = ":$min"
            }

            return "$hour$min $timeFormat"
        }
    }
}