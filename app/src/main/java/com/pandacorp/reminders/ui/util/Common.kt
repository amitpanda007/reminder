package com.pandacorp.reminders.ui.util

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

            if(hour > 12) {
                hour -= 12
                timeFormat = "PM"
            }else {
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