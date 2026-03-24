package com.example.dailybloom2.ui.hydration

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.dailybloom2.R

class HydrationReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "hydration_reminder_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            showHydrationReminder(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showHydrationReminder(context: Context) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Hydration Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminds you to drink water"
                    enableVibration(true)
                    enableLights(true)
                    
                    // Set custom sound
                    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    setSound(soundUri, AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Get default notification sound
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_hydration)
                .setContentTitle("💧 Time to Hydrate!")
                .setContentText("Don't forget to drink a glass of water to stay healthy!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 1000, 500, 1000))
                .setLights(0xFF4FC3F7.toInt(), 1000, 1000)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
