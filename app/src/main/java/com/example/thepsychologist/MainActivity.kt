package com.example.thepsychologist

import android.R.attr.value
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thepsychologist.Repository.ChatRepository
import com.example.thepsychologist.network.ApiClient
import com.example.thepsychologist.response.ChatRequest
import com.example.thepsychologist.response.ChatResponse
import com.example.thepsychologist.response.Message
import okhttp3.OkHttpClient
import okhttp3.internal.wait
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val messages = mutableListOf<MessageX>()
    private val chatRepository = ChatRepository(this)
    private val apiClient = ApiClient.getInstance()
    var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var questionText= findViewById<EditText>(R.id.question)
        var ok = findViewById<TextView>(R.id.okButton)

        var search = findViewById<TextView>(R.id.searchButton)

        if (intent.hasExtra("start")) {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter



        ok.setOnClickListener{
            var question = questionText.text.toString()
            messages.add(MessageX(question,true))
            adapter.notifyDataSetChanged()
            val completionFuture = createChatCompletion(question)
            completionFuture.thenApply { answer ->
                messages.add(MessageX(answer,false))
                adapter.notifyDataSetChanged()


                // Burada cevabı kullanabilirsin
                // Örneğin, cevabı bir değişkende saklayabilir veya başka bir işlem yapabilirsin.
            }.exceptionally { throwable ->
                // CompletableFuture'nin bir hata ile sonuçlanması durumunda çalışacak blok
                //println("Error occurred: ${throwable.message}")
                Log.e("Error","data cannot receive")
                // Hata durumunda yapılacak işlemler buraya yazılabilir.
                // Örneğin, hata mesajını loglamak, alternatif bir işlem yapmak gibi.
            }


            adapter.notifyDataSetChanged()


            }

            adapter.notifyDataSetChanged()


            search.setOnClickListener {

                Toast.makeText(this,"Search butona tıklandı",Toast.LENGTH_SHORT).show()


            }



        }
        else {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }


    private fun createChatCompletion(message : String ): CompletableFuture<String> {
        val future = CompletableFuture<String>()

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
            apiClient.creatChatCımpletion(chatRequest).enqueue(object :
                retrofit2.Callback<ChatResponse>
            {
                override fun onResponse(
                    call: retrofit2.Call<ChatResponse>,
                    response: retrofit2.Response<ChatResponse>
                ) {
                    val code = response.code()
                    if(code== 200) {

                        val answer = response.body()?.choices?.get(0)?.message?.content.toString()
                        future.complete(answer)
                        Log.d("Real answer" ,answer)

                    }
                    else {
                        future.completeExceptionally(Exception("Error response: ${response.errorBody()}"))

                        Log.d("error response",response.errorBody().toString())
                    }


                }

                override fun onFailure(call: retrofit2.Call<ChatResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

        }
        catch (e:Exception) {
            future.completeExceptionally(e)

            e.printStackTrace()
        }

        Log.d("Returning answer" , future.toString())
        return future
    }

   /* fun getResponse( question: String, callback:(String)-> Unit) {
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
    } */
}