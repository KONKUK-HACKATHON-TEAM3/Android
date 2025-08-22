package com.konkuk.hackathon_team3.presentation.main

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.konkuk.hackathon_team3.presentation.minseo.alarm.NotificationReceiver
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    // POST_NOTIFICATIONS(안드 13+) 런타임 권한 요청
    private val requestNotifications = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) scheduleNotificationAfter(10)  // 권한 허용 시 알람 예약
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()

        if (!isExactAlarmPermissionGranted()) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        } else {
            val hasPost = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPost) {
                requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                scheduleNotificationAfter(10)
            }
        }

        setContent {
            KONKUKHACKATHONTEAM3Theme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(
                        navController = navController,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    // 채널 생성
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "channel_id",
            "LocalNotificationChannel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "This is a channel for local notifications"
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    // n초 후 알람 예약
    private fun scheduleNotificationAfter(seconds: Int) {
        val timeInMillis = Calendar.getInstance().apply {
            add(Calendar.SECOND, seconds)
            // 특정 시각 사용 시:
            // set(Calendar.HOUR_OF_DAY, 12)
            // set(Calendar.MINUTE, 30)
            // set(Calendar.SECOND, 0)
        }.timeInMillis
        scheduleNotification(timeInMillis)
    }

    // AlarmManager로 리시버 호출 예약
    private fun scheduleNotification(timeInMillis: Long) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
    }

    // 정확한 알람 권한 확인
    private fun isExactAlarmPermissionGranted(): Boolean {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return am.canScheduleExactAlarms()
    }
}
