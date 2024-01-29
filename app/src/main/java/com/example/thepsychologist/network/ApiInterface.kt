package com.example.thepsychologist.network

import com.example.thepsychologist.appKey
import com.example.thepsychologist.response.ChatRequest
import com.example.thepsychologist.response.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("chat/completions")
    fun creatChatCımpletion(

        @Body chatRequest : ChatRequest,
        @Header("Content-Type") contentType : String = "application/json",
        @Header("Authorization") authorization : String = "Bearer $appKey",

        ): Call<ChatResponse>

}