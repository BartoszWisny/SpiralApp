package com.example.spiral

class FCMResponse {
    private var multicast_id = 0L
    private var success = 0
    private var failure = 0
    private var canonical_ids = 0
    private var results = ArrayList<FCMResult>()
    private var messageId = 0L
}