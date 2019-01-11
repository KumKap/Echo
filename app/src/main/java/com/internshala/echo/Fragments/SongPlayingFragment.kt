package com.internshala.echo.Fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.*
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.internshala.echo.CurrentSongHelper
import com.internshala.echo.Database.EchoDatabase
import com.internshala.echo.R
import com.internshala.echo.Songs
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.math.MathContext
import java.sql.Time
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 */
class SongPlayingFragment : Fragment() {

    object Statified{
        var myActivity : Activity? = null
        var mediaplayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView?= null
        var playPauseButtonImage: ImageButton?= null
        var previousButtonImage: ImageButton?=null
        var nextButtonImage: ImageButton?= null
        var loopButtonImage: ImageButton?= null
        var shuffleButtonImage: ImageButton?= null
        var seekbar: SeekBar? = null
        var songTitleView : TextView?=null
        var songArtistView : TextView?=null
        var currentSongHelper: CurrentSongHelper? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>?= null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var mSensorManager: SensorManager? = null
        var fab: ImageButton? = null
        var mSensorListener: SensorEventListener? = null

        var favoriteContent: EchoDatabase? = null

        var MY_PREFS_NAME = "ShakeFeature"
        var  updateSongTime = object: Runnable{
            override fun run() {
                val getcurrent = mediaplayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getcurrent?.toLong() as Long)-
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getcurrent?.toLong() as Long))))
                Handler().postDelayed(this, 1000)

            }
        }

    }


    object Staticated{
        var MY_PRES_SHUFFLE = "Shuffle Feature"
        var My_PRES_LOOP= "Loop Feature"

        fun playNext(check : String) {
            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition = Statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition

            }
            if (Statified.currentPosition == Statified.fetchSongs?.size) {
                Statified.currentPosition = 0
            }
            Statified.currentSongHelper?.isLoop = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songId = nextSong?.songID as Long
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist= nextSong?.artist
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            updateTextViews(Statified.currentSongHelper?.songArtist as String, Statified.currentSongHelper?.songTitle as String)

            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                processInformation(Statified.mediaplayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (Statified.favoriteContent?.checkifIdExists((Statified.currentSongHelper?.songId?.toInt() as Int))as Boolean)
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
            }else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

            }

        }



        fun onSongComplete() {
            if(Statified.currentSongHelper?.isShuffle as Boolean)
            {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            }
            else
            {
                if (Statified.currentSongHelper?.isLoop as Boolean) {

                    Statified.currentSongHelper?.isPlaying = true
                    val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.songId = nextSong?.songID as Long
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.artist
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition

                    updateTextViews(Statified.currentSongHelper?.songArtist as String, Statified.currentSongHelper?.songTitle as String)

                    Statified.mediaplayer?.reset()
                    try {
                        Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                        Statified.mediaplayer?.prepare()
                        Statified.mediaplayer?.start()
                        processInformation(Statified.mediaplayer as MediaPlayer)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else{
                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }
            if (Statified.favoriteContent?.checkifIdExists((Statified.currentSongHelper?.songId?.toInt() as Int))as Boolean)
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
            }else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

            }

        }


        fun updateTextViews(songArtist: String, songtitle: String){
            var songTitleUpdated = songtitle
            if(songtitle.equals("<unknown>", true)){
                songTitleUpdated = "unkown"
            }
            var songArtistUpdated = songArtist
            if(songArtist.equals("<unknown>", true)){
                songArtistUpdated = "unkown"
            }


            Statified.songTitleView?.setText(songTitleUpdated)
            Statified.songArtistView?.setText(songArtistUpdated)

        }
        fun processInformation(mediaplayer : MediaPlayer){
            val finalTime = mediaplayer.duration
            val startTime = mediaplayer.currentPosition
            Statified.seekbar?.max = finalTime
            Statified.startTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong())-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))))

            Statified.endTimeText?.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())-
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))))

            Statified.seekbar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)

        }
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        activity.title = "Now Playing"
        setHasOptionsMenu(true)

        Statified.seekbar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.nextButtonImage = view?.findViewById(R.id.nextButton)
        Statified.previousButtonImage = view?.findViewById(R.id.previousButton)
        Statified.loopButtonImage = view?.findViewById(R.id.loopButton)
        Statified.shuffleButtonImage = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.playPauseButtonImage = view?.findViewById(R.id.playPauseButton)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id. favoriteIcon)
        Statified.fab?.alpha = 0.8f

        return view

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualization = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
                Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {

        Statified.audioVisualization?.onPause()
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroy() {
                Statified.audioVisualization?.release()
        super.onDestroy()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAcceleration = 0.0f
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        bindShakeListener()

    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect ->{
                Statified.myActivity?.onBackPressed()
                return false
            }

        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favoriteContent = EchoDatabase(Statified.myActivity)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying= true
        Statified.currentSongHelper?.isLoop= false
        Statified.currentSongHelper?.isShuffle= false

        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songID: Long = 0
        try {

            path = arguments.getString("path")
            _songArtist = arguments.getString("songArtist")
            _songTitle = arguments.getString("songTitle")
            songID = arguments.getInt("songId").toLong()
            Statified.currentPosition = arguments.getInt("position")
            Statified.fetchSongs = arguments.getParcelableArrayList("songData")
            Statified.currentSongHelper?.songArtist= _songArtist
            Statified.currentSongHelper?.songPath= path
            Statified.currentSongHelper?.songId= songID
            Statified.currentSongHelper?.songTitle= _songTitle
            Statified.currentSongHelper?.currentPosition= Statified.currentPosition

            Staticated.updateTextViews(Statified.currentSongHelper?.songArtist as String, Statified.currentSongHelper?.songTitle as String)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments.get("FavBottomBar") as? String
        if(fromFavBottomBar != null)
        {
            Statified.mediaplayer = FavoriteFragment.Statified.mediaPlayer
        }else{
            Statified.mediaplayer?.start()
        }

        Statified.mediaplayer = MediaPlayer()
        Statified.mediaplayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(path))
            Statified.mediaplayer?.prepare()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        Statified.mediaplayer?.start()
        Staticated.processInformation(Statified.mediaplayer as MediaPlayer)

        if(Statified.currentSongHelper?.isPlaying as Boolean){
            Statified.mediaplayer?.pause()
            Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.pause_icon)
        }
        else{
            Statified.mediaplayer?.start()
            Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaplayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }
        clickHandler()
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

        var presForShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PRES_SHUFFLE,Context.MODE_PRIVATE )
        var isShuffleAllowed = presForShuffle?.getBoolean("feature", false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle=true
            Statified.currentSongHelper?.isLoop=false
            Statified.shuffleButtonImage?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_white_icon)
        }else{
            Statified.currentSongHelper?.isShuffle=false
            Statified.shuffleButtonImage?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        var presForLoop = Statified.myActivity?.getSharedPreferences(Staticated.My_PRES_LOOP,Context.MODE_PRIVATE )
        var isLoopAllowed = presForLoop?.getBoolean("feature", false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongHelper?.isShuffle=false
            Statified.currentSongHelper?.isLoop=true
            Statified.shuffleButtonImage?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_icon)
        }else{
            Statified.currentSongHelper?.isLoop=false
            Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_white_icon)
        }

        if (Statified.favoriteContent?.checkifIdExists((Statified.currentSongHelper?.songId?.toInt() as Int))as Boolean)
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
        }else
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

        }

    }

    fun clickHandler(){
        Statified.fab?.setOnClickListener({
        if (Statified.favoriteContent?.checkifIdExists((Statified.currentSongHelper?.songId?.toInt() as Int))as Boolean)
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))
            Statified.favoriteContent?.deleteFavorite(Statified.currentSongHelper?.songId?.toInt() as Int)
            Toast.makeText(Statified.myActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
        }else
        {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
            Statified.favoriteContent?.storeAsFavorite(Statified.currentSongHelper?.songId?.toInt(), Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle, Statified.currentSongHelper?.songPath)
            Toast.makeText(Statified.myActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()

        }})

        Statified.shuffleButtonImage?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PRES_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.My_PRES_LOOP, Context.MODE_PRIVATE)?.edit()

            if(Statified.currentSongHelper?.isShuffle as Boolean)
            {
                Statified.shuffleButtonImage?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }
            else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleButtonImage?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        Statified.loopButtonImage?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PRES_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.My_PRES_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLoop as Boolean)
            {
                Statified.currentSongHelper?.isLoop = false
                Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
            }
            else{
                Statified.currentSongHelper?.isShuffle = false
                Statified.currentSongHelper?.isLoop = true
                Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_icon)
                Statified.shuffleButtonImage?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
            }

        })


        Statified.nextButtonImage?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.pause_icon)
            if(Statified.currentSongHelper?.isShuffle as Boolean)
            {
                Staticated.playNext("PlayNextLikeNormalShuffle")
            }
            else
            {
                Staticated.playNext("PlayNextNormal")
            }
        })



        Statified.previousButtonImage?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLoop as Boolean)
            {
                Statified.loopButtonImage?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })


        Statified.playPauseButtonImage?.setOnClickListener({
            if(Statified.mediaplayer?.isPlaying as Boolean){
                Statified.mediaplayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.pause_icon)
            }
            else{
                Statified.mediaplayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.play_icon)
            }
        })
    }
        fun playPrevious() {

            Statified.currentPosition = Statified.currentPosition - 1
            if (Statified.currentPosition == -1) {
                Statified.currentPosition = 0
            }
            if (Statified.currentSongHelper?.isPlaying as Boolean) {
                Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.play_icon)
            }
            Statified.currentSongHelper?.isLoop = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songId = nextSong?.songID as Long
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTextViews(Statified.currentSongHelper?.songArtist as String, Statified.currentSongHelper?.songTitle as String)

            Statified.mediaplayer?.reset()
            try {
                Statified.mediaplayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
                Statified.mediaplayer?.prepare()
                Statified.mediaplayer?.start()
                Staticated.processInformation(Statified.mediaplayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()

            }

            if (Statified.favoriteContent?.checkifIdExists((Statified.currentSongHelper?.songId?.toInt() as Int))as Boolean)
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_on))
            }else
            {
                Statified.fab?.setImageDrawable(ContextCompat.getDrawable(Statified.myActivity, R.drawable.favorite_off))

            }

        }


    fun bindShakeListener(){
        Statified.mSensorListener = object: SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]


                mAccelerationLast=mAccelerationCurrent
                mAccelerationCurrent = Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration*0.9f + delta

                if(mAcceleration > 12){
                    val prefs= Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if(isAllowed as Boolean){
                    Staticated.playNext("PlayNextNormal")
                        }
                }
            }

        }
    }


}// Required empty public constructor
