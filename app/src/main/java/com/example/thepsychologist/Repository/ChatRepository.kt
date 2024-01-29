package com.example.thepsychologist.Repository

import android.util.Log
import com.example.thepsychologist.network.ApiClient
import com.example.thepsychologist.response.ChatRequest
import com.example.thepsychologist.response.ChatResponse
import com.example.thepsychologist.response.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRepository {

    private val apiClient = ApiClient.getInstance()

    fun createChatCompletion(message : String ) {

            try {
                val chatRequest = ChatRequest(
                    arrayListOf(
                        Message(
                            "You are a Psychologist.You answer me like a Psychologist",
                            "system"

                    ),
                        Message(
                            message,
                            "user"
                        )
                    ),
                    "gpt-3.5-turbo"
                )
                apiClient.creatChatCımpletion(chatRequest).enqueue(object : Callback<ChatResponse>
                {
                    override fun onResponse(
                        call: Call<ChatResponse>,
                        response: Response<ChatResponse>
                    ) {
                        val code = response.code()
                        if(code== 200) {
                            response.body()?.choices?.get(0)?.message?.let{
                                Log.d("message",it.toString())
                            }
                        }
                        else {
                            Log.d("error response",response.errorBody().toString())
                        }
                    }

                    override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })

            }
            catch (e:Exception) {
                e.printStackTrace()
            }
        }



}