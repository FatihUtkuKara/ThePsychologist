package com.example.thepsychologist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.factory.thepsychologist.R
import android.widget.TextView

class GuideActivity : AppCompatActivity() {
    companion object {
        private lateinit var mContext: Context

        fun initializeContext(context: Context) {
            mContext = context
        }
    }
    private lateinit var goToAppButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        initialize()

        goToAppButton = findViewById(R.id.goToAppButton)

        goToAppButton.setOnClickListener{

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