package com.pandacorp.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcaster: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(LOG_TAG, "INTENT TIME REACHED FOR REMINDER !!!")

        val reminderText = intent!!.extras!!.getString("reminder")
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context,
            "notifyReminder"
        )
            .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
            .setContentTitle(reminderText)
            .setContentText("")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(200, builder.build())
        }
    }
}