package com.bahaddindemir.bitcointicker.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bahaddindemir.bitcointicker.BitcoinTickerApplication.Companion.CHANNEL_ID
import com.bahaddindemir.bitcointicker.BuildConfig
import com.bahaddindemir.bitcointicker.R

class BackgroundRefreshService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val isStart = intent.getBooleanExtra("isStart", false)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}