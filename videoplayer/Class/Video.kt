package com.app.videoplayer.Class

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.app.videoplayer.Activity.HomeActivity
import java.io.File


data class Video(val id: String, var title: String, val folderName: String, val path: String, val artUri: Uri)

data class folders(var id : String,var flnam :String)



