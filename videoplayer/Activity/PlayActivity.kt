package com.app.videoplayer.Activity

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.*
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.app.videoplayer.Activity.HomeActivity.Companion.videoList
import com.app.videoplayer.Class.Video
import com.app.videoplayer.R
import com.app.videoplayer.databinding.ActivityPlayBinding
import com.app.videoplayer.databinding.BoosterBinding
import com.app.videoplayer.databinding.MoreFeactureBinding
import com.app.videoplayer.databinding.SpeedDialogBinding
import com.github.vkay94.dtpv.youtube.YouTubeOverlay
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs


class PlayActivity : AppCompatActivity(),  GestureDetector.OnGestureListener {

    private lateinit var binding: ActivityPlayBinding

    lateinit var playerlist: ArrayList<Video>
    private lateinit var playPauseBtn: ImageButton
    private lateinit var fullScreenBtn: ImageButton
    private lateinit var videoTitle: TextView
    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    private var minSwipeY: Float = 0f

    companion object {
        private var audioManager: AudioManager? = null
        lateinit var play: ExoPlayer
        private var timer: Timer? = null
        var position = -1
        var repeat: Boolean = false
        private var isfullscreen: Boolean = false
        private var isLocked: Boolean = false
        private var brightness: Int = 0
        private var volume: Int = 0
        @SuppressLint("StaticFieldLeak")
        private lateinit var trackSelector: DefaultTrackSelector
        private lateinit var loudnessEnhancer: LoudnessEnhancer
        private var speed: Float = 1.0f
        var pipStatus: Int = 0
        private var isSpeedChecked: Boolean = false

    }

    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPlayBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        videoTitle = findViewById(R.id.videoTitle)
        playPauseBtn = findViewById(R.id.playPauseBtn)
        fullScreenBtn = findViewById(R.id.fullScreenBtn)

        gestureDetectorCompat = GestureDetectorCompat(this, this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager


        //for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        playerlist = ArrayList()
        initilizationlayout()
        initilizationbinding()
        playvideo()


    }




    private fun initilizationlayout() {

        when (intent.getStringExtra("class")) {
            "AllVideo" -> {
                playerlist = ArrayList()
                playerlist.addAll(videoList)
                createPlayer()
            }
            "NowPlaying" ->
            {
                speed = 1.0f
                videoTitle.text = playerlist[position].title
                videoTitle.isSelected = true
                doubleTapEnable()
                playvideo()
                playInfullscreen(enable = isfullscreen)
                seekBarFeacture()
            }

        }
        if (repeat) findViewById<ImageButton>(R.id.repeatBtn).setImageResource(R.drawable.baseline_repeat_24)
        else
            findViewById<ImageButton>(R.id.repeatBtn).setImageResource(com.google.android.exoplayer2.R.drawable.exo_controls_repeat_off)

    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun initilizationbinding() {

        findViewById<ImageButton>(R.id.orientationBtn).setOnClickListener {
            requestedOrientation =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }



        findViewById<ImageButton>(R.id.playPauseBtn).setOnClickListener {
            if (play.isPlaying) pausevideo()
            else playvideo()
        }
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener {
            finish()

        }
        findViewById<ImageButton>(R.id.nextBtn).setOnClickListener {
            nextprevideo()
            findViewById<ImageButton>(R.id.prevBtn).setOnClickListener { nextprevideo(isNext = false) }
            findViewById<ImageButton>(R.id.repeatBtn).setOnClickListener {
                if (repeat) {
                    repeat = false
                    play.repeatMode = Player.REPEAT_MODE_OFF
                    findViewById<ImageButton>(R.id.repeatBtn).setImageResource(com.google.android.exoplayer2.R.drawable.exo_controls_repeat_off)
                } else {
                    repeat = true
                    play.repeatMode = Player.REPEAT_MODE_ONE
                    findViewById<ImageButton>(R.id.repeatBtn).setImageResource(com.google.android.exoplayer2.R.drawable.exo_controls_repeat_all)
                }
            }
            findViewById<ImageButton>(R.id.fullScreenBtn).setOnClickListener {

                if (isfullscreen) {
                    isfullscreen = false
                    playInfullscreen(enable = false)
                } else {
                    isfullscreen = true
                    playInfullscreen(enable = true)
                }

            }
        }
        binding.lockButton.setOnClickListener {
            if (!isLocked) {
                // for hinding
                isLocked = true
                binding.playerView.hideController()
                binding.playerView.useController = false
                binding.lockButton.setImageResource(R.drawable.baseline_lock_close)
            } else {
                // for showing
                isLocked = false
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.lockButton.setImageResource(R.drawable.baseline_lock_open_24)
            }
        }
        findViewById<ImageButton>(R.id.moreFeaturesBtn).setOnClickListener {
            pausevideo()
            val customDialog =
                LayoutInflater.from(this).inflate(R.layout.more_feacture, binding.root, false)
            val bindingMF = MoreFeactureBinding.bind(customDialog)
            val dialog = AlertDialog.Builder(this).setView(customDialog)

                .create()
            dialog.show()


            bindingMF.audioBooster.setOnClickListener {
                dialog.dismiss()
                val customDialogB = LayoutInflater.from(this).inflate(R.layout.booster, binding.root, false)
                val bindingB = BoosterBinding.bind(customDialogB)
                val dialogB = AlertDialog.Builder(this).setView(customDialogB)
                    .setOnCancelListener { playvideo() }
                    .setPositiveButton("OK"){self, _ ->
                        loudnessEnhancer.setTargetGain(bindingB.verticalBar.progress * 100)
                        playvideo()
                        self.dismiss()
                    }
                    .create()
                dialogB.show()
                bindingB.verticalBar.progress = loudnessEnhancer.targetGain.toInt()/100
                bindingB.progressText.text = "Audio Boost\n\n${loudnessEnhancer.targetGain.toInt()/10} %"
                bindingB.verticalBar.setOnProgressChangeListener {
                    bindingB.progressText.text = "Audio Boost\n\n${it*10} %"
                }
            }


            bindingMF.speedBtn.setOnClickListener {
                dialog.dismiss()
                playvideo()
                val customDialogs =
                    LayoutInflater.from(this).inflate(R.layout.speed_dialog, binding.root, false)
                val bindings = SpeedDialogBinding.bind(customDialogs)
                val dialogs = AlertDialog.Builder(this).setView(customDialogs)
                    .setCancelable(false)
                    .setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->

                        dialogInterface.dismiss()
                    })
                    .create()
                dialogs.show()
                bindings.speedText.text = "${DecimalFormat("#.##").format(speed)} X"
                bindings.minusBtn.setOnClickListener {
                    changeSpeed(isIncrement = false)
                    bindings.speedText.text = "${DecimalFormat("#.##").format(speed)} X"
                }
                bindings.plusBtn.setOnClickListener {
                    changeSpeed(isIncrement = true)
                    bindings.speedText.text = "${DecimalFormat("#.##").format(speed)} X"
                }
                bindings.speedCheckBox.isChecked = isSpeedChecked
                bindings.speedCheckBox.setOnClickListener {
                    it as CheckBox
                    isSpeedChecked = it.isChecked
                }
            }
            bindingMF.sleepTimer.setOnClickListener {
                dialog.show()
                if (timer != null) Toast.makeText(
                    applicationContext,
                    "Timer Already Running!!/nClose App To Reset Timer!!",
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    var sleepTime = 15
                    val customDialogs = LayoutInflater.from(this)
                        .inflate(R.layout.speed_dialog, binding.root, false)
                    val bindings = SpeedDialogBinding.bind(customDialogs)
                    val dialogs = AlertDialog.Builder(this).setView(customDialogs)
                        .setCancelable(false)
                        .setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int ->
                            timer = Timer()
                            val task = object : TimerTask() {
                                override fun run() {
                                    moveTaskToBack(true)
                                    onDestroy()
                                }
                            }
                            timer!!.schedule(task, sleepTime * 60 * 1000.toLong())
                            playvideo()
                            dialogInterface.dismiss()
                        })
                        .create()
                    dialogs.show()
                    bindings.speedText.text = "$sleepTime Min"
                    bindings.minusBtn.setOnClickListener {
                        if (sleepTime > 15) sleepTime -= 15
                        bindings.speedText.text = "$sleepTime Min"
                    }
                    bindings.plusBtn.setOnClickListener {
                        if (sleepTime < 120) sleepTime += 15
                        bindings.speedText.text = "$sleepTime Min"
                    }
                }
            }
            bindingMF.pipModeBtn.setOnClickListener {
                val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        android.os.Process.myUid(),
                        packageName
                    ) ==
                            AppOpsManager.MODE_ALLOWED
                } else {
                    false
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (status) {
                        this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                        dialog.dismiss()
                        playvideo()
                        pipStatus = 0
                    } else {
                        val intent = Intent(
                            "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }

                } else {
                    Toast.makeText(this, "Feature Not Supported!!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    playvideo()
                }

            }

            bindingMF.shareBtn.setOnClickListener {
                var uri = Uri.parse(videoList[position].path)
                var intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "video/*"
                startActivity(Intent.createChooser(intent, "share via"))
            }
            bindingMF.propertiesbtn.setOnClickListener {

                showProperties(position)

            }

        }
    }

    private fun createPlayer() {
        try { play.release() }catch (e: Exception){}
        speed = 1.0f

        try {
            trackSelector = DefaultTrackSelector(this)
            videoTitle.text = playerlist[position].title
            videoTitle.isSelected = true
            play = ExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
            doubleTapEnable()
            val mediaItem = MediaItem.fromUri(playerlist[position].artUri)
            play.setMediaItem(mediaItem)
            play.prepare()
            playvideo()
            play.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED) nextprevideo()

                }
            })
            playInfullscreen(enable = isfullscreen)
            loudnessEnhancer = LoudnessEnhancer(play.audioSessionId)
            loudnessEnhancer.enabled = true
            seekBarFeacture()

            binding.playerView.setControllerVisibilityListener {

                when {
                    isLocked -> binding.lockButton.visibility = View.VISIBLE
                    binding.playerView.isControllerVisible -> binding.lockButton.visibility =
                        View.VISIBLE
                    else -> binding.lockButton.visibility = View.INVISIBLE
                }

            }
            play.play()

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }

    }



    private fun playvideo() {

        findViewById<ImageButton>(R.id.playPauseBtn).setImageResource(R.drawable.baseline_pause_24)
        play.play()
        doubleTapEnable()
    }

    private fun pausevideo() {
        findViewById<ImageButton>(R.id.playPauseBtn).setImageResource(R.drawable.baseline_play_arrow_24)
        play.pause()
        doubleTapEnable()

    }


    private fun nextprevideo(isNext: Boolean = true) {
        if (isNext) setposition()
        else setposition(isIncrement = false)
        createPlayer()

    }

    private fun setposition(isIncrement: Boolean = true) {
        if (!repeat) {
            if (isIncrement) {
                if (playerlist.size - 1 == position)
                    position = 0
                else
                    ++position

            } else {
                if (position == 0)
                    position = playerlist.size - 1
                else
                    --position
            }
        }
    }

    private fun playInfullscreen(enable: Boolean) {
        if (enable) {
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            play.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            findViewById<ImageButton>(R.id.fullScreenBtn).setImageResource(R.drawable.baseline_fullscreen_exit_24)
        } else {
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            play.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            findViewById<ImageButton>(R.id.fullScreenBtn).setImageResource(R.drawable.baseline_fullscreen_24)
        }


    }


    private fun changeSpeed(isIncrement: Boolean) {
        if (isIncrement) {
            if (speed <= 2.9f) {
                speed += 0.10f //speed = speed + 0.10f
            }
        } else {
            if (speed > 0.20f) {
                speed -= 0.10f
            }
        }
        play.setPlaybackSpeed(speed)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPictureInPictureMode(PictureInPictureParams.Builder()
            .setAspectRatio(Rational(2, 3))
            .build())
    }




    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {

        if (isInPictureInPictureMode)
        {
            if(pipStatus != 0){

                val intent = Intent(this, PlayActivity::class.java)
                when(pipStatus){
                    1 -> intent.putExtra("class","AllVideo")

                }

                startActivity(intent)
            }
            if(!isInPictureInPictureMode) pausevideo()


        }

    }
   /* override fun onAudioFocusChange(p0: Int) {
        if(p0 <= 0) pausevideo()
    }

    override fun onResume() {
        super.onResume()
        if(audioManager == null) audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if(brightness != 0) setScreenBrightness(brightness)
    } */


    @SuppressLint("ClickableViewAccessibility")
    private fun doubleTapEnable() {
        binding.playerView.player = play
        binding.ytOverlay.performListener(object :YouTubeOverlay.PerformListener{
            override fun onAnimationEnd() {
                binding.ytOverlay.visibility = View.GONE
            }

            override fun onAnimationStart() {
                binding.ytOverlay.visibility = View.VISIBLE
            }

        })
        binding.ytOverlay.player(play)
            binding.playerView.setOnTouchListener { _, motionEvent ->
                binding.playerView.isDoubleTapEnabled = false
               if (!isLocked){
                   binding.playerView.isDoubleTapEnabled = true
                   gestureDetectorCompat.onTouchEvent(motionEvent)
                   if(motionEvent.action == MotionEvent.ACTION_UP) {


                       //for immersive mode
                       WindowCompat.setDecorFitsSystemWindows(window, false)
                       WindowInsetsControllerCompat(window, binding.root).let { controller ->
                           controller.hide(WindowInsetsCompat.Type.systemBars())
                           controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                       }
                   }
               }

                return@setOnTouchListener false
            }


    }
    private fun seekBarFeacture(){
        findViewById<DefaultTimeBar>(com.github.vkay94.dtpv.R.id.exo_progress).addListener(object :TimeBar.OnScrubListener{
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                pausevideo()

            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                play.seekTo(position)

            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                playvideo()

            }

        })
    }


    override fun onDown(p0: MotionEvent): Boolean {
        minSwipeY = 0f
        return false
    }
    override fun onShowPress(p0: MotionEvent) = Unit
    override fun onSingleTapUp(p0: MotionEvent): Boolean = false
    override fun onLongPress(p0: MotionEvent) = Unit
    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean = false

    override fun onScroll(event: MotionEvent, event1: MotionEvent, distanceX: Float, distanceY: Float): Boolean {

        minSwipeY += distanceY
        val sWidth = Resources.getSystem().displayMetrics.widthPixels
        val sHeight = Resources.getSystem().displayMetrics.heightPixels

        val border = 100 * Resources.getSystem().displayMetrics.density.toInt()
        if(event.x < border || event.y < border || event.x > sWidth - border || event.y > sHeight - border)
            return false
        if (abs(distanceX) < abs(distanceY)){
            if (event.x < sWidth/2){
                //brightness
                val increase = distanceY > 0
                val newValue = if(increase) brightness + 1 else brightness - 1
                if(newValue in 0..50) brightness = newValue
                binding.brightnessIcon.text = brightness.toString()
                setScreenBrightness(brightness)

            }
            else{
                // volume
                val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val minVolume = 0
                val increase = distanceY > 0
                val newValue = if (increase) volume + 1 else volume - 1
                volume = newValue.coerceIn(minVolume, maxVolume)
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                binding.volumeIcon.text = volume.toString()

            }
            minSwipeY = 0f
        }
        return true
    }
    private fun setScreenBrightness(value: Int){
        val layoutParams = window.attributes
        layoutParams.screenBrightness = value / 255.0f
        window.attributes = layoutParams
    }
    private fun showProperties(position: Int)
    {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.file_properties)
        var name:String = videoList[position].title
        var path:String = videoList[position].path
        var tit:TextView = dialog.findViewById(R.id.pro_title)
        var pt:TextView = dialog.findViewById(R.id.pro_path)

        tit.text = name
        pt.text = path


        dialog.show()


    }

    override fun onDestroy() {
        super.onDestroy()
        play.release()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        pausevideo()
    }




}







