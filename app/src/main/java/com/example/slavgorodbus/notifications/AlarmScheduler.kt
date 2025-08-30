package com.example.slavgorodbus.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.slavgorodbus.data.model.FavoriteTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object AlarmScheduler {

    private const val ALARM_REQUEST_CODE_PREFIX = "fav_alarm_"
    private const val FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000L

    fun scheduleAlarm(context: Context, favoriteTime: FavoriteTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerAtMillis = calculateNextDepartureTimeInMillis(favoriteTime) - FIVE_MINUTES_IN_MILLIS

        if (triggerAtMillis <= System.currentTimeMillis()) {
            Log.d("AlarmScheduler", "Alarm time for ${favoriteTime.id} (${favoriteTime.routeName} at ${favoriteTime.departureTime}) is in the past or too soon. Not scheduling.")
            return
        }

        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = "com.example.slavgorodbus.ALARM_TRIGGER_${favoriteTime.id}" // Уникальное действие
            putExtra("FAVORITE_ID", favoriteTime.id)
            putExtra("ROUTE_INFO", "${favoriteTime.routeNumber} ${favoriteTime.routeName}".trim())
            putExtra("DEPARTURE_TIME_INFO", "в ${favoriteTime.departureTime}")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            (ALARM_REQUEST_CODE_PREFIX + favoriteTime.id).hashCode(), // Уникальный requestCode
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                    Log.d("AlarmScheduler", "Exact alarm scheduled for ${favoriteTime.id} at ${formatMillis(triggerAtMillis)}")
                } else {
                    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis, 60000L, pendingIntent) // Окно в 1 минуту
                    Log.w("AlarmScheduler", "Exact alarms not permitted. Scheduled inexact alarm for ${favoriteTime.id} around ${formatMillis(triggerAtMillis)}")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                Log.d("AlarmScheduler", "Alarm scheduled for ${favoriteTime.id} at ${formatMillis(triggerAtMillis)}")
            }
        } catch (se: SecurityException) {
            Log.e("AlarmScheduler", "SecurityException: Cannot schedule alarm for ${favoriteTime.id}. Check permissions (e.g., SCHEDULE_EXACT_ALARM).", se)
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule alarm for ${favoriteTime.id}", e)
        }
    }

    fun cancelAlarm(context: Context, favoriteTimeId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = "com.example.slavgorodbus.ALARM_TRIGGER_${favoriteTimeId}"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            (ALARM_REQUEST_CODE_PREFIX + favoriteTimeId).hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            try {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel() // Отменяем сам PendingIntent
                Log.d("AlarmScheduler", "Alarm cancelled for $favoriteTimeId")
            } catch (e: Exception) {
                Log.e("AlarmScheduler", "Error cancelling alarm for $favoriteTimeId", e)
            }
        } else {
            Log.d("AlarmScheduler", "No alarm found to cancel for $favoriteTimeId (PendingIntent was null)")
        }
    }

    private fun calculateNextDepartureTimeInMillis(favoriteTime: FavoriteTime): Long {
        val (hour, minute) = try {
            favoriteTime.departureTime.split(":").map { it.toInt() }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Invalid departure time format: ${favoriteTime.departureTime} for ID ${favoriteTime.id}", e)
            return -1L
        }

        if (hour !in 0..23 || minute !in 0..59) {
            Log.e("AlarmScheduler", "Invalid time values: hour=$hour, minute=$minute for ID ${favoriteTime.id}")
            return -1L
        }

        val targetDayOfWeek = favoriteTime.dayOfWeek

        if (targetDayOfWeek !in Calendar.SUNDAY..Calendar.SATURDAY) {
            Log.e("AlarmScheduler", "Invalid dayOfWeek: $targetDayOfWeek for ID ${favoriteTime.id}. Expected ${Calendar.SUNDAY}-${Calendar.SATURDAY}.")
            return -1L
        }

        val now = Calendar.getInstance()
        val nextDeparture = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (now.get(Calendar.DAY_OF_WEEK) == targetDayOfWeek && now.after(nextDeparture)) {
            nextDeparture.add(Calendar.DAY_OF_YEAR, 7) // Добавляем 7 дней
        } else {
            while (nextDeparture.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek || nextDeparture.before(now)) {
                nextDeparture.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        Log.d("AlarmScheduler", "Calculated next departure for ${favoriteTime.id} (${favoriteTime.departureTime}, day $targetDayOfWeek): ${formatMillis(nextDeparture.timeInMillis)}")
        return nextDeparture.timeInMillis
    }

    private fun formatMillis(millis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(millis)
    }
}