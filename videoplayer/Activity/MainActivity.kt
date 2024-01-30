package com.app.videoplayer.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.app.videoplayer.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        Handler().postDelayed(Runnable {

            startActivity(Intent(applicationContext,StartActivity::class.java))

        },6000)
    }
}