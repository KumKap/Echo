package com.internshala.echo.utils

import android.app.Service
import android.bluetooth.BluetoothClass
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.internshala.echo.Fragments.SongPlayingFragment
import com.internshala.echo.R
import com.internshala.echo.activities.MainActivity

/**
 * Created by USER on 02-09-2018.
 */
class CaptureBroadcast: BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action == Intent.ACTION_NEW_OUTGOING_CALL){
            try {
                MainActivity.Statisfied.notificationManager?.cancel(1978)
            }catch (e: Exception){e.printStackTrace()}

            try {
                if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Statified.mediaplayer?.pause()
                    SongPlayingFragment.Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.pause_icon)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        else {
            val tm: TelephonyManager = p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try {
                        MainActivity.Statisfied.notificationManager?.cancel(1978)
                    }catch (e: Exception){e.printStackTrace()}
                    try {
                        if (SongPlayingFragment.Statified.mediaplayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Statified.mediaplayer?.pause()
                            SongPlayingFragment.Statified.playPauseButtonImage?.setBackgroundResource(R.drawable.pause_icon)
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                else -> {

                }

            }
        }
    }

}