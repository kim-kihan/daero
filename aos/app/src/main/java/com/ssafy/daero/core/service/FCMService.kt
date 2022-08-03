package com.ssafy.daero.core.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.daero.application.App
import com.ssafy.daero.application.MainActivity
import com.ssafy.daero.data.dto.user.FCMTokenRequestDto
import com.ssafy.daero.data.repository.UserRepository
import com.ssafy.daero.utils.notification.getNotificationBuilder

class FCMService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService_DaeRo", "onNewToken: 토큰 갱신: $token")
        App.prefs.ftoken = token

        if (App.prefs.userSeq != 0 && App.prefs.jwt != null) {
            UserRepository.get().updateFcmToken(App.prefs.userSeq, FCMTokenRequestDto(token))
                .subscribe({}, { throwable ->
                    Log.d("FCMService_DaeRo", throwable.toString())
                })
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            NotificationManagerCompat.from(this).notify(
                101,
                getNotificationBuilder(
                    "daero",
                    it.title ?: "",
                    it.body ?: "",
                    pIntent
                ).build()
            )
        }
    }
}