package com.tenko.app.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

fun scheduleMedicationAlarm(context: Context, startDate: LocalDate?, endDate: LocalDate?, hour: Int, minute: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedicationReceiver::class.java).apply {
        putExtra("title", "Hora de tu medicamento")
        putExtra("startDate", startDate.toString())
        putExtra("endDate", endDate.toString())
    }

    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val systemZone = ZoneId.systemDefault()
    val zonedDateTime = LocalDate.now()
        .atTime(hour, minute)
        .atZone(systemZone)

    val triggerTime = zonedDateTime.toInstant()

    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        triggerTime.toEpochMilli(),
        AlarmManager.INTERVAL_DAY, // Se repite cada día
        pendingIntent
    )
}