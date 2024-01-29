package com.example.thepsychologist.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiClient {


    @Volatile
    private var INSTANCE : ApiInterface ? = null

    fun getInstance() : ApiInterface{
        synchronized(this ) {
            return INSTANCE ?: Retrofit.Builder()
                //TODO check baseurl
                .baseUrl("https://api.openai.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
                .also {
                    INSTANCE = it
                }

        }
    }
}