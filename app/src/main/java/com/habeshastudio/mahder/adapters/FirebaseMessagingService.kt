package com.habeshastudio.mahder.adapters

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.habeshastudio.mahder.R

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val notification_title = remoteMessage!!.notification.title
        val notification_message = remoteMessage.notification.body

        val click_action = remoteMessage.notification.clickAction

        val from_user_id = remoteMessage.data["from_user_id"]

        val mBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification_title)
                .setContentText(notification_message)


        val resultIntent = Intent(click_action)
        resultIntent.putExtra("user_id", from_user_id)


        val resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder.setContentIntent(resultPendingIntent)


        val mNotificationId = System.currentTimeMillis().toInt()

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotifyMgr.notify(mNotificationId, mBuilder.build())


    }

}

