package com.example.project

import com.example.project.Notifications.MyResponse
import com.example.project.Notifications.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=${BuildConfig.CHAT_API_KEY}"  // Using BuildConfig.CHAT_API_KEY
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender): Call<MyResponse>
}