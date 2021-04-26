package com.pandacorp.reminders

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcaster: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(LOG_TAG, "INTENT TIME REACHED FOR REMINDER !!!")

        val reminderText = intent!!.extras!!.getString("reminder")
        val notificationId = intent!!.extras!!.getInt("notificationId")

        val actionIntent = Intent(context, MainActivity::class.java)
        val actionPendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, actionIntent, 0)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context,
            "notifyReminder"
        )
            .setSmallIcon(R.drawable.ic_add_reminder)
            .setContentTitle(reminderText)
            .setContentText("")
            .addAction(R.mipmap.ic_launcher, "Show Reminders", actionPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            if (notificationId != null) {
                notify(notificationId, builder.build())
            }
        }
    }
}