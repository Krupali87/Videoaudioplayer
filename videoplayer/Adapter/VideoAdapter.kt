package com.app.videoplayer.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.app.videoplayer.Activity.PlayActivity
import com.app.videoplayer.Class.Video
import com.app.videoplayer.R



class VideoAdapter(var context: Context, var videoList: ArrayList<Video>) : RecyclerView.Adapter<VideoAdapter.MyViewHolder>() {

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var videoimage: ImageView = itemView.findViewById(R.id.videoImg)
            var title: TextView = itemView.findViewById(R.id.txtvideoname)
            var foldername: TextView = itemView.findViewById(R.id.txtfoldername)
            var  rootview: View = itemView


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            var view = LayoutInflater.from(context).inflate(R.layout.video_view, parent, false)
            return MyViewHolder(view)
        }

        @SuppressLint("SuspiciousIndentation")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.videoimage.setImageResource(R.drawable.youtube)
            holder.title.text = videoList[position].title
            holder.foldername.text = videoList[position].folderName
            holder.rootview.setOnClickListener {
                when {
                    else -> {
                        PlayActivity.pipStatus = 1
                        sendIntent(pos = position, ref = "AllVideo")

                    }
                }
            }

        }

        override fun getItemCount(): Int {

            return videoList.size
        }

        private fun sendIntent(pos: Int, ref: String) {

            PlayActivity.position = pos
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra("class", ref)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
}


