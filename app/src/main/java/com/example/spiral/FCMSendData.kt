package com.example.spiral

class FCMSendData {
    private var to: String
    private var data: Map<String, String>

    constructor(to: String, data: Map<String, String>) {
        this.to = to
        this.data = data
    }

    fun getTo(): String {
        return to
    }

    fun setTo(receiver: String) {
        this.to = receiver
    }

    fun getData(): Map<String, String> {
        return data
    }

    fun setData(data: Map<String, String>) {
        this.data = data
    }
}
