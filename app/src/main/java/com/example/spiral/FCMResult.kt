package com.example.spiral

class FCMResult {
    private var message_id = ""

    fun getMessageId(): String {
        return message_id
    }

    fun setMessageId(messageId: String) {
        this.message_id = messageId
    }
}
