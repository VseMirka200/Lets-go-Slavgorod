package com.example.slavgorodbus.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e("AlarmReceiver", "Context or Intent is null.")
            return
        }

        Log.d("AlarmReceiver", "Alarm received with action: ${intent.action}")

        val favoriteId = intent.getStringExtra("FAVORITE_ID")
        val routeInfo = intent.getStringExtra("ROUTE_INFO") ?: "Ваш автобус"
        val departureTimeInfo = intent.getStringExtra("DEPARTURE_TIME_INFO") ?: ""

        if (favoriteId == null) {
            Log.e("AlarmReceiver", "Favorite ID is missing in the intent.")
            return
        }

        NotificationHelper.createNotificationChannel(context)
        NotificationHelper.showDepartureNotification(
            context,
            favoriteId,
            routeInfo,
            departureTimeInfo
        )

        Log.d("AlarmReceiver", "Notification should be shown for $favoriteId, $routeInfo, $departureTimeInfo")
    }
}