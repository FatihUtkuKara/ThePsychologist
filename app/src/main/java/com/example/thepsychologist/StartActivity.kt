package com.example.thepsychologist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class StartActivity : AppCompatActivity() {
    companion object {
        private lateinit var mContext: Context

        fun initializeContext(context: Context) {
            mContext = context
        }
    }
    private lateinit var skipButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        initialize()

        skipButton = findViewById(R.id.skipButton)


        skipButton.setOnClickListener{

            val intent = Intent(mContext, PayActivity::class.java)
            intent.putExtra("start","start" )
            mContext.startActivity(intent)
            overridePendingTransition(R.anim.slide_left, R.anim.no_animation)
        }
    }

    private fun initialize() {
        initializeContext(this)
    }
}