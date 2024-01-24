package com.example.thepsychologist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val messages = mutableListOf<MessageX>()
    private lateinit var okButton:TextView
    private lateinit var questionText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent.hasExtra("start")) {
            questionText= findViewById<EditText>(R.id.question)

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
            getResponse(question)
        }
    }

    fun getResponse( question: String) {

    }
}