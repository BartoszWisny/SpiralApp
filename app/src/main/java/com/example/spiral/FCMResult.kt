package com.example.spiral

class FCMResult {
    private lateinit var message_id: String

    fun getMessageId(): String {
        return message_id
    }

    fun setMessageId(messageId: String) {
        this.message_id = messageId
    }
}
