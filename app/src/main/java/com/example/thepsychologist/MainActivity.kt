package com.example.thepsychologist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thepsychologist.Repository.ChatRepository
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val messages = mutableListOf<MessageX>()
    private val chatRepository = ChatRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var questionText= findViewById<EditText>(R.id.question)
        var okButton = findViewById<TextView>(R.id.okButton)

        if (intent.hasExtra("start")) {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter


        messages.add(MessageX("Merhaba, nasılsın?", true))
        messages.add(MessageX("Merhaba! Ben iyiyim, sen nasılsın?", false))
        messages.add(MessageX("Ben de iyiyim, teşekkür ederim!", true))
        messages.add(MessageX("Ben de iyiyim, teşekkür ederim!", false))


        adapter.notifyDataSetChanged()
        }
        else {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }

        okButton.setOnClickListener{
            var question = questionText.text.toString()

            createChatCompletion(question)
            /*getResponse(question) {response ->
               runOnUiThread{
                   messages.add(MessageX(response,false))
               }
            } */
        }
    }

    fun createChatCompletion(message : String ) {

        chatRepository.createChatCompletion(message)
    }

    fun getResponse( question: String, callback:(String)-> Unit) {
        val apiKey="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"
        val url="\n" + "https://api.openai.com/v1/chat/completions"

        val requestBody = """
            {
                "model": "gpt-3.5-turbo", 
                "messages": [
                  {
                    "role": "system",
                    "content": "You are a helpful assistant."
                  },
                  {
                    "role": "user",
                    "content": $question
                  }
    ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody( "application/json".toMediaTypeOrNull()))
            .build()


        client.newCall(request).enqueue(object  : Callback
        {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                if( body != null) {
                    Log.v("data", body.toString())


                }
                else {
                    Log.v("data", "empty")

                }
            }


        })
    }
}