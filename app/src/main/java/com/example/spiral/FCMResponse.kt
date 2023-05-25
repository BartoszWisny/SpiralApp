package com.example.spiral

class FCMResponse {
    private var muticast_id = 0L
    private var success = 0
    private var failure = 0
    private var canonical_ids = 0
    private var results = ArrayList<FCMResult>()
    private var messageId = 0L

    fun getMulticastId(): Long {
        return muticast_id
    }

    fun setMulticastId(multicastId: Long) {
        this.muticast_id = multicastId
    }

    fun getSuccess(): Int {
        return success
    }

    fun setSuccess(success: Int) {
        this.success = success
    }

    fun getFailure(): Int {
        return failure
    }

    fun setFailure(failure: Int) {
        this.failure = failure
    }

    fun getCanonicalIds(): Int {
        return canonical_ids
    }

    fun setCanonicalIds(canonicalIds: Int) {
        this.canonical_ids = canonicalIds
    }

    fun getResults(): ArrayList<FCMResult> {
        return results
    }

    fun setResults(results: ArrayList<FCMResult>) {
        this.results = results
    }

    fun getMessageId(): Long {
        return messageId
    }

    fun setMessageId(messageId: Long) {
        this.messageId = messageId
    }
}