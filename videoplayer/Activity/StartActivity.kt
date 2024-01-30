package com.app.videoplayer.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityPlaylistDetailBinding
import com.app.videoplayer.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnvideo.setOnClickListener {

            startActivity(Intent(applicationContext,HomeActivity::class.java))
        }
        binding.btnmusic.setOnClickListener {
            startActivity(Intent(applicationContext,MusicActivity::class.java))
        }



    }
}