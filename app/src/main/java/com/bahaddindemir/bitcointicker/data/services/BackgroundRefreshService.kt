package com.bahaddindemir.bitcointicker.data.services

import android.content.Intent
import android.os.IBinder
import android.app.Notification
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.app.Service
import androidx.annotation.Nullable
import com.bahaddindemir.bitcointicker.BitcoinTickerApplication.Companion.CHANNEL_ID
import com.bahaddindemir.bitcointicker.BuildConfig
import com.bahaddindemir.bitcointicker.R
import com.readystatesoftware.chuck.internal.ui.MainActivity

class BackgroundRefreshService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val isStart = intent.getBooleanExtra("isStart", false)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Background refresh service working")
            .setSmallIcon(R.drawable.ic_fg)
            .build()
        if (isStart) {
            if (BuildConfig.DEBUG) {
                startForeground(1, notification)
            }
        } else {
            stopForeground(true)
        }
        return START_NOT_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}