package com.example.spiral

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFCMClient {
    companion object {
        private var instance: Retrofit? = null

        fun getInstance(): Retrofit {
            return if (instance == null) Retrofit.Builder().baseUrl("https://fcm.googleapis.com/").addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build() else instance!!
        }
    }
}