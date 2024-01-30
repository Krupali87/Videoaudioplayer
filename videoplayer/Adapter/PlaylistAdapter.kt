package com.app.videoplayer.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.videoplayer.Activity.MusicPlaylistActivity
import com.app.videoplayer.Activity.PlaylistDetailActivity
import com.app.videoplayer.Class.Playlist
import com.app.videoplayer.R

class PlaylistAdapter(var context:Context,var playList: ArrayList<Playlist>) : RecyclerView.Adapter<PlaylistAdapter.MyplaylistHolder>()
{
   inner class MyplaylistHolder(itemView : View) :RecyclerView.ViewHolder(itemView){

       var playlistimg : ImageView = itemView.findViewById(R.id.playlistImg)
       var playlistname : TextView = itemView.findViewById(R.id.playlistName)
       var deletemusic : ImageButton = itemView.findViewById(R.id.playlistDeleteBtn)
       val rootview: View = itemView


   }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistAdapter.MyplaylistHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.playlist_view,parent,false)
        return MyplaylistHolder(view)
    }




    override fun onBindViewHolder(holder: PlaylistAdapter.MyplaylistHolder, position: Int) {
       holder.playlistname.text = playList[position].name
        holder.playlistname.isSelected = true
        holder.deletemusic.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(playList[position].name)
                .setMessage("Do you want to delete playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    MusicPlaylistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }
        holder.rootview.setOnClickListener {

            val intent = Intent(context,PlaylistDetailActivity::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
        return playList.size
    }
    fun refreshPlaylist()
    {
        playList = ArrayList()
        playList.addAll(MusicPlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}