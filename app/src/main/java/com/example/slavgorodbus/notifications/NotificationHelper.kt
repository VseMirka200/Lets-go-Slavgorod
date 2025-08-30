package com.example.slavgorodbus.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.slavgorodbus.MainActivity
import com.example.slavgorodbus.R

object NotificationHelper {
    private const val CHANNEL_ID = "bus_departure_channel"
    private const val CHANNEL_NAME = "Уведомления об отправлении"
    private const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о скором отправлении автобуса"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationHelper", "Notification channel created/updated.")
        }
    }

    fun showDepartureNotification(
        context: Context,
        favoriteTimeId: String,
        routeInfo: String,
        departureTimeInfo: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentRequestCode = favoriteTimeId.hashCode()
        val pendingIntent = PendingIntent.getActivity(
            context,
            pendingIntentRequestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val smallIconResId = R.drawable.ic_stat_directions_bus

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconResId)
            .setContentTitle("$routeInfo отправляется!")
            .setContentText("Автобус отходит $departureTimeInfo. Не опаздывайте.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val uniqueNotificationId = NOTIFICATION_ID_BASE + favoriteTimeId.hashCode()
        notificationManager.notify(uniqueNotificationId, notification)
        Log.d("NotificationHelper", "Notification shown with ID $uniqueNotificationId for $favoriteTimeId")
    }
}
