package com.example.thepsychologist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thepsychologist.database.ChatDataSource
import com.factory.thepsychologist.R

class HistoryActivity : AppCompatActivity() {
    private val messagesContent = mutableListOf<MessageContent>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val dataSource = ChatDataSource(this)
        dataSource.open()
        val allMessages: List<Pair<Int, String>> = dataSource.getAllMessages()

        val recyclerView = findViewById<RecyclerView>(R.id.rvHistory)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = ChatAdapter(messagesContent)
        recyclerView.adapter = adapter



         var reversedList = allMessages.reversed()
        for (pair in reversedList) {
            val userId = pair.first
            val message = pair.second

            if ( userId == 1) {

                messagesContent.add(MessageContent(message,true))
                adapter.notifyDataSetChanged()

                println("User ID: $userId, Message: $message")
            }

            else if ( userId == 2){
                messagesContent.add(MessageContent(message,false))
                adapter.notifyDataSetChanged()


            }
    }
}
}