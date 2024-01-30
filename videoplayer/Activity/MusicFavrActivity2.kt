package com.app.videoplayer.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.videoplayer.Adapter.FavouriteAdapter
import com.app.videoplayer.Adapter.MusicAdapter
import com.app.videoplayer.Class.Music
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityMusicFavr2Binding
import com.app.videoplayer.databinding.ActivityMusicPlayerBinding

class MusicFavrActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMusicFavr2Binding
    private lateinit var adapter: FavouriteAdapter

    companion object {
        var favouriteSongs : ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicFavr2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Favourite Music"

        binding.backBtnFA.setOnClickListener { finish() }
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(this,4)
        adapter = FavouriteAdapter(this, favouriteSongs)
       binding.favouriteRV.adapter = adapter

        if (favouriteSongs.size <1) binding.shuffleBtnFA.visibility = View.VISIBLE
        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(applicationContext,MusicPlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","FavouriteShuffle")
            startActivity(intent)



        }

    }
}