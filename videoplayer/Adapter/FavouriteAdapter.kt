package com.app.videoplayer.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.videoplayer.Activity.MusicPlayerActivity
import com.app.videoplayer.Class.Music
import com.app.videoplayer.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FavouriteAdapter(private var context: Context,private var musicList:ArrayList<Music>) : RecyclerView.Adapter<FavouriteAdapter.MyViewHolder12>() {
   inner class MyViewHolder12(itemView : View) : RecyclerView.ViewHolder(itemView) {

       var songimage : ImageView = itemView.findViewById(R.id.songImgFV)
       var songname : TextView = itemView.findViewById(R.id.songNameFV)
       val rootview: View = itemView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder12 {

        val view = LayoutInflater.from(context).inflate(R.layout.favourite_view, parent, false)
        return MyViewHolder12(view)

    }

    override fun getItemCount(): Int {
       return musicList.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder12, position: Int) {

     holder.songname.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.applymusic)).centerCrop()
            .into(holder.songimage)
    }
}