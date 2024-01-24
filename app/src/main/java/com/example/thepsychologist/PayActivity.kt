package com.example.thepsychologist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PayActivity : AppCompatActivity() {

    companion object {
        private lateinit var mContext: Context

        fun initializeContext(context: Context) {
            mContext = context
        }
    }
    private lateinit var makePayButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        if (intent.hasExtra("start")) {
            initialize()
            makePayButton = findViewById(R.id.makePayButton)


            makePayButton.setOnClickListener{

                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("start","start" )
                mContext.startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up, R.anim.no_animation)
            }

        }

    }

    private fun initialize() {
        initializeContext(this)
    }
}