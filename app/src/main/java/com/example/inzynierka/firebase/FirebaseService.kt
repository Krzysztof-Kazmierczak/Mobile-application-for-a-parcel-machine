package com.example.inzynierka.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.inzynierka.R
import com.example.inzynierka.aktywnosci.App.Companion.CHANNEL_ID
import com.example.inzynierka.aktywnosci.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlin.random.Random

class FirebaseService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        var zmienna = "asd"
        zmienna = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i("MyTag", "onMessageReceived ${Gson().toJson(message)}")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("title", message.data["title"])
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createChannel(notificationManager)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createChannel(notificationManager: NotificationManager){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(CHANNEL_ID,"ExampleServiceChannel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Description"
                enableLights(true)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(serviceChannel)
        }

    }
}