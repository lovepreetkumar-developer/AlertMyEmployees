package com.alert.me.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.alert.me.R
import com.alert.me.data.preferences.PreferenceProvider
import com.alert.me.ui.UserHomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class FirebaseMessagingService : FirebaseMessagingService(), KodeinAware {

    override val kodein by kodein()

    private val mPrefProvider: PreferenceProvider by instance()

    private var mNotificationManager: NotificationManager? = null
    private var broadcaster: LocalBroadcastManager? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        /*
        Firebase will send following kind of data in notification
        value[0] = "Message of the notification"
        value[1] = "icon"
        value[2] = "Title of the notification"
        value[3] = "USER_HOME_SCREEN"*/

        if (mPrefProvider.isUserLoggedIn()) {
            // playing audio and vibration when user se request
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                r.isLooping = false
            }

            val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            builder.setSmallIcon(R.drawable.ic_notification)
            val resultIntent = Intent(this, UserHomeActivity::class.java)

            val bundle = Bundle()
            bundle.putString("title", remoteMessage.data["title"])
            bundle.putString("message", remoteMessage.data["message"])

            resultIntent.putExtras(bundle)

            val pIntent =
                PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setContentTitle(remoteMessage.data["title"])
            builder.setContentText(remoteMessage.data["message"])
            builder.setContentIntent(pIntent)
            builder.setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    remoteMessage.data["message"]
                )
            )
            builder.setAutoCancel(true)
            builder.priority = Notification.PRIORITY_MAX
            mNotificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "Your_channel_id"
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH
                )
                mNotificationManager!!.createNotificationChannel(channel)
                builder.setChannelId(channelId)
            }

            //notificationId is a unique int for each notification that you must define
            mNotificationManager!!.notify(100, builder.build())

            val bundle1 = Bundle()
            bundle1.putString("title", remoteMessage.data["title"])
            bundle1.putString("message", remoteMessage.data["message"])
            val intent = Intent("MyData")
            intent.putExtras(bundle1)
            broadcaster?.sendBroadcast(intent)
        }
    }
}