package com.example.spiral

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val dataReceived: Map<String, String> = message.data
        chat.showNotification(applicationContext, dataReceived["sender"].hashCode(), dataReceived["title"],
            dataReceived["content"], dataReceived["sender"], dataReceived["receiver"], dataReceived["room_id"], null)
    }
}