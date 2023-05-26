package com.example.spiral

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAmYJzH1Q:APA91bHA7pQHEXy1HuuZBYlJZSRoQRnBXTSivObftLWlYyEj9nxFttVjN_yajjnE5Y4u6jo8_SrIQ29kc2vjWukUmUPzwdqG9-_-JN9zZUAQAO-poi5cO6UW9guqvk7Nxt7HSdHOGlzz"
    )

    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData): Observable<FCMResponse>
}