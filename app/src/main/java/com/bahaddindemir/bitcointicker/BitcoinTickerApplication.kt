package com.bahaddindemir.bitcointicker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BitcoinTickerApplication : Application() {
    companion object {
        const val CHANNEL_ID = "exampleServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()

        Hawk.init(applicationContext).build()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )!!
            manager.createNotificationChannel(serviceChannel)
        }
    }
}