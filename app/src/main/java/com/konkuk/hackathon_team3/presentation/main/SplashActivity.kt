package com.konkuk.hackathon_team3.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.konkuk.hackathon_team3.presentation.minseo.alarm.NotificationReceiver
import com.konkuk.hackathon_team3.ui.theme.KONKUKHACKATHONTEAM3Theme
import kotlinx.coroutines.delay
import java.util.Calendar

@SuppressLint("CustomSplashScreen")
class SplashActivity() : ComponentActivity() {
    private val requestNotifications = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) scheduleNotificationAfter(10)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
//                scheduleNotificationAfter(10)
            }
        }

        setContent {
            KONKUKHACKATHONTEAM3Theme {
                SplashScreen(
                    onFinish = {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    // 채널 생성
    private fun createNotificationChannel() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel_gas_v1"

        if (nm.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId,
                "생존신고 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "생존신고 리마인더"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 100, 300)

                // res/raw/alarm_sample_sound.mp3 를 채널 사운드로
                val soundUri =
                    "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/raw/alarm_sample_sound".toUri()
                val attrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(soundUri, attrs)
            }
            nm.createNotificationChannel(channel)
        }
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
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
    }

    // 정확한 알림 권한 확인
    private fun isExactAlarmPermissionGranted(): Boolean {
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        return am.canScheduleExactAlarms()
    }
}

@Composable
private fun SplashScreen(
    onFinish: () -> Unit,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Start.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(composition) {
        if (composition != null) {
            delay(5_800)
            onFinish()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )
    }
}
