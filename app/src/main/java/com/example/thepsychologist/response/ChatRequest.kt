package com.example.thepsychologist.response

data class ChatRequest(
    val messages: List<Message>,
    val model : String
)
