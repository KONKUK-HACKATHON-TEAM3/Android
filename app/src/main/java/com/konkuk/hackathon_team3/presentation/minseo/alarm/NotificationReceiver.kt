package com.konkuk.hackathon_team3.presentation.minseo.alarm

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.konkuk.hackathon_team3.R
import com.konkuk.hackathon_team3.presentation.main.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // 권한 확인(권한이 없으면 알림을 표시하지 않음)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val clickIntent = Intent(context, MainActivity::class.java).apply {
                putExtra("from_notification", true)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val clickPendingIntent = PendingIntent.getActivity(
                context,
                1001,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 알림 생성
            val notification = NotificationCompat.Builder(it, "channel_gas_v1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("가스")
                .setContentText("생존 신고를 아직 작성하지 않으셨네요. 작성하러 가볼까요?")
                .setAutoCancel(true)
                .setContentIntent(clickPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            // 알림 표시
            with(NotificationManagerCompat.from(it)) {
                notify(1001, notification)
            }
        }
    }
}