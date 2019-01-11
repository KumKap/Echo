package com.internshala.echo.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.internshala.echo.Adapters.NavigationDrawerAdapter
import com.internshala.echo.Fragments.MainScreenFragment
import com.internshala.echo.Fragments.SongPlayingFragment
import com.internshala.echo.R

class MainActivity : AppCompatActivity(){
    var navigationDrawerIconList: ArrayList<String> = arrayListOf()
    var  images_for_navdrawer = intArrayOf(R.drawable.navigation_allsongs,
            R.drawable.navigation_favorites,R.drawable.navigation_settings,
            R.drawable.navigation_aboutus)
    var trackNotificationBuilder: Notification? = null

    object Statisfied{
        var drawerLayout : DrawerLayout? = null
        var notificationManager: NotificationManager? = null


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        MainActivity.Statisfied.drawerLayout = findViewById(R.id.drawer_layout)
        navigationDrawerIconList.add("All Songs")
        navigationDrawerIconList.add("Favorites")
        navigationDrawerIconList.add("Settings")
        navigationDrawerIconList.add("About Us")


        val toggle = ActionBarDrawerToggle(this@MainActivity,MainActivity.Statisfied.drawerLayout,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        MainActivity.Statisfied.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        val mainscreenfragment= MainScreenFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment,mainscreenfragment,"MainScreenFragment")
                .commit()

        var _navigationAdapter = NavigationDrawerAdapter(navigationDrawerIconList, images_for_navdrawer,
                this)
        _navigationAdapter.notifyDataSetChanged()

        var navigation_recycler_view=findViewById<RecyclerView>(R.id.navigation_recycler_view)
        navigation_recycler_view.layoutManager=LinearLayoutManager(this)
        navigation_recycler_view.itemAnimator=DefaultItemAnimator()
        navigation_recycler_view.adapter=_navigationAdapter
        navigation_recycler_view.setHasFixedSize(true)

        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(this@MainActivity, System.currentTimeMillis().toInt(),
                intent, 0)
        trackNotificationBuilder = Notification.Builder(this)
                .setContentTitle("A track is playing in the background")
                .setSmallIcon(R.drawable.echo_logo)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build()
        Statisfied.notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    override fun onStart() {
        super.onStart()
        try {
            Statisfied.notificationManager?.cancel(1978)
        }catch (e: Exception){e.printStackTrace()}
    }

    override fun onStop() {
        super.onStop()
        try{
            if(SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean){
                Statisfied.notificationManager?.notify(1978,trackNotificationBuilder)
            }
        }
        catch (e: Exception){e.printStackTrace()}
    }

    override fun onResume() {
        super.onResume()
        try {
            Statisfied.notificationManager?.cancel(1978)
        }catch (e: Exception){e.printStackTrace()}
    }

}
