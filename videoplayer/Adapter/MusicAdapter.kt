package com.app.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.videoplayer.Activity.MusicPlayerActivity
import com.app.videoplayer.Class.Music
import com.app.videoplayer.Class.formateDuration

import com.app.videoplayer.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class MusicAdapter(var context: Context,var musicList :ArrayList<Music>) : RecyclerView.Adapter<MusicAdapter.MyMusicHolder>() {
   inner class MyMusicHolder(itemView: View) :RecyclerView.ViewHolder(itemView){

       var musicImage : ImageView = itemView.findViewById(R.id.imageMV)
       var musicname :TextView = itemView.findViewById(R.id.songNameMV)
       var musicalbum :TextView = itemView.findViewById(R.id.songAlbumMV)
       var musicduration :TextView = itemView.findViewById(R.id.songDuration)
       val rootview: View = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyMusicHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_view, parent, false)
        return MyMusicHolder(view)
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyMusicHolder, position: Int) {

       holder.musicname.text = musicList[position].title
        holder.musicalbum.text = musicList[position].album
        holder.musicduration.text = formateDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.applymusic)).centerCrop()
            .into(holder.musicImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,MusicPlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","MusicAdapter")
           ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
       return musicList.size
    }
}