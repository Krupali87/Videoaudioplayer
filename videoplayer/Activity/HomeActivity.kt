package com.app.videoplayer.Activity

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.videoplayer.Adapter.VideoAdapter
import com.app.videoplayer.Class.Video
import com.app.videoplayer.Class.folders
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityHomeBinding


import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    companion object {

        lateinit var videoList: ArrayList<Video>
        lateinit var folderList: ArrayList<folders>
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        if (requestRuntimePermission()){

            folderList = ArrayList()
            videoList = getAllVideos()

        }

        binding.recycler.setHasFixedSize(true)
        binding.recycler.setItemViewCacheSize(10)
        binding.recycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.recycler.adapter = VideoAdapter(applicationContext, videoList)
        binding.txttotal.text = "Total Videos :  ${videoList.size}"





    }


    private fun requestRuntimePermission() : Boolean{

        if (ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),101)

            return false
        }
        return true

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                videoList = getAllVideos()
                folderList = ArrayList()
            }

            else
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),101)

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("InlinedApi", "Recycle", "Range")

    private fun getAllVideos() : ArrayList<Video> {
        val templist = ArrayList<Video>()
        val tempFolder = ArrayList<String>()
        val projecation = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.BUCKET_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projecation,
            null,
            null,
            MediaStore.Video.Media.DATE_ADDED + " DESC"
        )

        if (cursor != null)
            if (cursor.moveToNext())
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val folderidC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                    val foldernameC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

                    try {

                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val video = Video(
                            title = titleC,
                            id = idC,
                            folderName = foldernameC,
                            path = pathC,
                            artUri = artUriC
                        )
                        if (file!!.exists()) templist.add(video)

                        // adding folder

                        if (!tempFolder.contains(foldernameC)) {
                            tempFolder.add(foldernameC)
                            folderList.add(folders(id = folderidC, flnam = foldernameC))

                        }

                    } catch (e: Exception) {
                    }

                } while (cursor.moveToNext())
        cursor?.close()
        return templist
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.option,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.exit ->
            {
               finishAffinity()
            }

        }
        return super.onOptionsItemSelected(item)
    }





}