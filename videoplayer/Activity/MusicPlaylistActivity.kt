package com.app.videoplayer.Activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.app.videoplayer.Adapter.PlaylistAdapter
import com.app.videoplayer.Class.MusicPlaylist
import com.app.videoplayer.Class.Playlist
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityMusicPlaylistBinding
import com.app.videoplayer.databinding.AddPlaylistDialogBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MusicPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlaylistBinding
    private lateinit var adapter: PlaylistAdapter

    companion object
    {
        var musicPlaylist : MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlaylistBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Music Playlist"

       binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this,2)
        adapter = PlaylistAdapter(this, playList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter
        binding.addPlaylistBtn.setOnClickListener { customAlertDialog() }

        binding.backBtnPLA.setOnClickListener { finish() }

    }
    private fun customAlertDialog()
    {
        var customdialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog,binding.root,false)
        val binder = AddPlaylistDialogBinding.bind(customdialog)
        val builder = AlertDialog.Builder(this)
        builder.setView(customdialog)
        builder.setTitle("Exit")
            .setMessage("Playlist Detail")
            .setPositiveButton("ADD",{ dialogInterface: DialogInterface, i: Int ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if (playlistName != null && createdBy != null) {
                    if (playlistName.isNotEmpty() && createdBy.isNotEmpty())
                    {
                        addPlaylist(playlistName.toString(),createdBy.toString())
                    }
                        dialogInterface.dismiss()
                }
            }).show()

    }


    private fun addPlaylist(name: String, createdBy: String) {

        var playlistExits = false
        for (i in musicPlaylist.ref)
        {
            if(name == i.name)
            {
                playlistExits = true
                break
            }
        }
        if (playlistExits) Toast.makeText(applicationContext,"playlist exist",Toast.LENGTH_SHORT).show()
        else {
            val tempPlaylist = Playlist()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.createdBy = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn = sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()

        }
    }
}
