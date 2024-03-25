package com.example.thepsychologist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thepsychologist.Repository.ChatRepository
import com.example.thepsychologist.database.ChatDataSource
import com.example.thepsychologist.network.ApiClient
import com.example.thepsychologist.response.ChatRequest
import com.example.thepsychologist.response.ChatResponse
import com.example.thepsychologist.response.Message
import com.factory.thepsychologist.R
import okhttp3.OkHttpClient
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private val messagesContent = mutableListOf<MessageContent>()
    private val chatRepository = ChatRepository(this)
    private val apiClient = ApiClient.getInstance()
    var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var questionText = findViewById<EditText>(R.id.question)
        var ok = findViewById<ImageView>(R.id.okButton)
        var historyButton = findViewById<TextView>(R.id.burger)

        var search = findViewById<TextView>(R.id.burger)

        if (intent.hasExtra("start")) {


            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            val adapter = ChatAdapter(messagesContent)
            recyclerView.adapter = adapter

            recyclerView.scrollToPosition(adapter.itemCount-1)

            val dataSource = ChatDataSource(this)
            dataSource.open()
            val allMessages: List<Pair<Int, String>> = dataSource.getAllMessages()

            var reversedList = allMessages.reversed()
            for (pair in reversedList) {
                val userId = pair.first
                val message = pair.second

                if ( userId == 1) {

                    messagesContent.add(MessageContent(message,true))
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(adapter.itemCount-1)

                    println("User ID: $userId, Message: $message")
                }

                else if ( userId == 2){
                    messagesContent.add(MessageContent(message,false))
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(adapter.itemCount-1)


                }
            }
            historyButton.setOnClickListener {
                dataSource.close()
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }



            ok.setOnClickListener {
                var question = questionText.text.toString()
                messagesContent.add(MessageContent(question, true))
                adapter.notifyDataSetChanged()
                questionText.text.clear()
                dataSource.addMessage(1, question)
                val completionFuture = createChatCompletion(question)
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                // true döndürerek event'in diğer listener'lara iletilmemesini sağlayın
                true
                completionFuture.thenApply { answer ->
                    messagesContent.add(MessageContent(answer, false))
                    dataSource.addMessage(2, answer)
                    adapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(adapter.itemCount-1)

                    val messages: List<Pair<Int, String>> = dataSource.getAllMessages()


                    for (message in messages) {
                        Log.d("Burada", "Message: $message")
                    }

                }.exceptionally { throwable ->

                    Log.e("Error", "data cannot receive")

                }

                adapter.notifyDataSetChanged()

            }

            adapter.notifyDataSetChanged()

        } else {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }


    private fun createChatCompletion(message: String): CompletableFuture<String> {
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
                retrofit2.Callback<ChatResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ChatResponse>,
                    response: retrofit2.Response<ChatResponse>
                ) {
                    val code = response.code()
                    if (code == 200) {

                        val answer = response.body()?.choices?.get(0)?.message?.content.toString()
                        future.complete(answer)
                        Log.d("Real answer", answer)

                    } else {
                        future.completeExceptionally(Exception("Error response: ${response.errorBody()}"))

                        Log.d("error response", response.errorBody().toString())
                    }


                }

                override fun onFailure(call: retrofit2.Call<ChatResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

        } catch (e: Exception) {
            future.completeExceptionally(e)

            e.printStackTrace()
        }

        Log.d("Returning answer", future.toString())
        return future
    }


}