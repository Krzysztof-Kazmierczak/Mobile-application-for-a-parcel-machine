package com.example.inzynierka.firebase

data class NotificationData(
    val title: String,
    val message: String,
    val count: Int,
    val bool: Boolean,
)

data class PushNotification(
    val data: NotificationData,
    val to: String
)