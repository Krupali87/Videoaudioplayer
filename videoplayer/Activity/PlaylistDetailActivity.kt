package com.app.videoplayer.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.videoplayer.Adapter.MusicAdapter
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityPlaylistDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailBinding
    private lateinit var adapter: MusicAdapter

    companion object
    {
        var currentPlaylistposition:Int = -1
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.color.cool_pink)
        binding = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Music Detail"


        currentPlaylistposition = intent.extras?.get("index") as Int

        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.setItemViewCacheSize(13)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this,)
        MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].playlist.addAll(MusicActivity.musicList)
        MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].playlist.shuffle()
       adapter = MusicAdapter(this,MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].playlist)
        binding.playlistDetailsRV.adapter = adapter

        binding.backBtnPD.setOnClickListener { finish() }

        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, MusicPlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }





    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        binding.playlistNamePD.text = MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].name
        binding.moreinfo.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].createdOn}\n\n" +
                "  -- ${MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].createdBy}"
        if(adapter.itemCount > 0)
        {
            Glide.with(this)
                .load(MusicPlaylistActivity.musicPlaylist.ref[currentPlaylistposition].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.applymusic).centerCrop())
                .into(binding.playlistimgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
    }
}