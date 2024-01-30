package com.app.videoplayer.Activity


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.videoplayer.Adapter.MusicAdapter
import com.app.videoplayer.Class.Music
import com.app.videoplayer.R
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import java.io.File


class MusicActivity : AppCompatActivity() {


    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicrv : RecyclerView
    private lateinit var shuffleButton : Button
    private lateinit var favourButton : Button
    private lateinit var playlistButton : Button

    companion object {

        lateinit var musicList : ArrayList<Music>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        supportActionBar?.title = "All Music"

        shuffleButton = findViewById(R.id.shuffleBtn)
        favourButton = findViewById(R.id.favouriteBtn)
        playlistButton = findViewById(R.id.playlistBtn)

        if (requestRuntimePermission())
            initilization()

        shuffleButton.setOnClickListener {

            val intent = Intent(applicationContext,MusicPlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MusicActivity")
            startActivity(intent)

        }
        favourButton.setOnClickListener {
            val intent = Intent(applicationContext,MusicFavrActivity2::class.java)
            startActivity(intent)

        }
        playlistButton.setOnClickListener {
            val intent = Intent(applicationContext,MusicPlaylistActivity::class.java)
            startActivity(intent)

        }

        initilization()
        //for retriveing favourites data using shared preferences
        MusicFavrActivity2.favouriteSongs = ArrayList()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("FavouriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
        if(jsonString != null){
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
            MusicFavrActivity2.favouriteSongs.addAll(data)
        }





    }

    private fun requestRuntimePermission() : Boolean{

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            return false
        }
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode==13)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Grand", Toast.LENGTH_SHORT).show()
                initilization()
            }

            else
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            }
        }
    }

    private fun initilization()
    {
        musicrv = findViewById(R.id.musicRv)
        requestRuntimePermission()
        musicList = getAllMusic()
        musicrv.setHasFixedSize(true)
        musicrv.setItemViewCacheSize(13)
        musicrv.layoutManager = LinearLayoutManager(this)
        musicAdapter = MusicAdapter(this,musicList)
        musicrv.adapter = musicAdapter

    }

    @SuppressLint("Recycle", "Range")
    private fun getAllMusic() : ArrayList<Music>
    {
        val tempList = ArrayList<Music>()
        val selecation = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projecation = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
                            MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projecation,selecation,null,null , null)
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse( "content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri,albumIdC).toString()
                    val mu =Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, artUri = artUriC)
                    val file = File(mu.path)
                    if (file.exists())
                    {
                        tempList.add(mu)
                    }




                }while (cursor.moveToNext())
                cursor.close()
            }
        }


        return tempList




    }

    override fun onResume() {
        super.onResume()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(MusicFavrActivity2.favouriteSongs)
        editor.putString("FavouriteSongs",jsonString)
        editor.apply()

    }






}

