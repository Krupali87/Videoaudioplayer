package com.app.videoplayer.Activity



import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.videoplayer.Class.Music
import com.app.videoplayer.Class.favourtiChker
import com.app.videoplayer.Class.formateDuration
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityMusicPlayerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class MusicPlayerActivity : AppCompatActivity(),MediaPlayer.OnCompletionListener  {

    private lateinit var binding: ActivityMusicPlayerBinding
    private var mediaPlayer: MediaPlayer? = null

    companion object {

        lateinit var musicList : ArrayList<Music>
        var songPosition: Int = 0
        private var mediaPlayer: MediaPlayer? = null
        private var isPlaying: Boolean = false
        private lateinit var runnable : Runnable
        var repeat : Boolean = false
        var isFavourite: Boolean = false
        var fIndex: Int = -1
        val handler = Handler()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Music Player"
        initilization()
        mediaPlayer = MediaPlayer()
        seekBarSetup()
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.seekTo(0)

        binding.playPauseBtnPA.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()

        }
        binding.backbutton.setOnClickListener {
            pauseMusic()
            finish()
        }

        binding.previousBtnPA.setOnClickListener { prevNextSong(increment = false) }
        binding.nextBtnPA.setOnClickListener {prevNextSong(increment = true)}
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat)
            {
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            }
            else {
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }
        }

        binding.seekBarPA.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
              if (mediaPlayer!= null && fromUser)
                  mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }


        })


        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(musicList[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Shareing Music File"))
        }
        binding.favbtn.setOnClickListener {
                if (isFavourite)
                {
                    isFavourite = false
                    binding.favbtn.setImageResource(R.drawable.baseline_favorite_border_24)
                    MusicFavrActivity2.favouriteSongs.removeAt(fIndex)
                }
            else {

                    isFavourite = true
                    binding.favbtn.setImageResource(R.drawable.baseline_favorite_24)
                    MusicFavrActivity2.favouriteSongs.add(musicList[songPosition])
            }
        }
    }

    private fun SetLayout(){

        fIndex = favourtiChker(musicList[songPosition].id)
        Glide.with(this)
            .load(musicList[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.applymusic)).centerCrop()
            .into(binding.songImgPA)
        binding.songNamePA.text = musicList[songPosition].title
        if (repeat)  binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        if (isFavourite) binding.favbtn.setImageResource(R.drawable.baseline_favorite_24)
        else binding.favbtn.setImageResource(R.drawable.baseline_favorite_border_24)

    }
    @SuppressLint("SuspiciousIndentation")
    private fun createMediaPlayer()
    {
      try {
          if (mediaPlayer == null) mediaPlayer = MediaPlayer()
          mediaPlayer!!.reset()
          mediaPlayer!!.setDataSource(musicList[songPosition].path)
          mediaPlayer!!.prepare()
         mediaPlayer!!.start()
          isPlaying = true
          binding.playPauseBtnPA.setIconResource(R.drawable.baseline_stop_24)
            binding.tvSeekBarStart.text = formateDuration(mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formateDuration(mediaPlayer!!.duration.toLong())
            seekBarSetup()
          mediaPlayer!!.setOnCompletionListener(this)
      } catch(e: Exception){return}

    }
    private fun initilization()
    {
        mediaPlayer = MediaPlayer()
        songPosition = intent.getIntExtra("index",0)
        when (intent.getStringExtra("class")){
            "MusicAdapter"-> {
                musicList = ArrayList()
                musicList.addAll(MusicActivity.musicList)
                SetLayout()
            }
            "MusicActivity" -> {
                musicList = ArrayList()
                musicList.addAll(MusicActivity.musicList)
                musicList.shuffle()
                SetLayout()
            }
            "FavouriteShuffle" ->{
                musicList = ArrayList()
                musicList.addAll(MusicFavrActivity2.favouriteSongs)
                musicList.shuffle()
                SetLayout()
            }
            "PlaylistDetailsShuffle"->{
                musicList = ArrayList()
                musicList.addAll(MusicFavrActivity2.favouriteSongs)
                musicList.shuffle()
                SetLayout()
            }
        }


    }

    private fun playMusic()
    {
        binding.playPauseBtnPA.setIconResource(R.drawable.baseline_stop_24)
        isPlaying = true

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()

        }

        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(musicList[songPosition].path)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = mediaPlayer!!.duration


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun pauseMusic()
    {
        binding.playPauseBtnPA.setIconResource(R.drawable.baseline_play_arrow_24)
        isPlaying = false
       mediaPlayer?.pause()
    }
    private fun prevNextSong(increment : Boolean)
    {

            if (increment)
            {
                SetSongPosition(increment = true)
                SetLayout()
                createMediaPlayer()
            }
            else {
                SetSongPosition(increment = false)
                SetLayout()
                createMediaPlayer()
            }
        }

    private fun SetSongPosition(increment: Boolean)
    {
        if (!repeat)
        {
            if (increment){
                if (musicList.size-1 == songPosition)
                    songPosition = 0
                else
                    ++songPosition

            }else {
                if (songPosition== 0)
                    songPosition = musicList.size- 1
                else
                    --songPosition
            }
        }
       }

    override fun onCompletion(p0: MediaPlayer?) {
        SetSongPosition(increment = true)
        createMediaPlayer()
        try {
            SetLayout()
        }catch (e:Exception){return}
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK)
            return
    }


    fun seekBarSetup() {
        if (mediaPlayer != null) {
            runnable = Runnable {
                binding.tvSeekBarStart.text = formateDuration(mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text = formateDuration(mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = mediaPlayer!!.currentPosition
                Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
            }
            Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        pauseMusic()
    }
    }



