package com.example.naamadrory

import android.app.Application
import com.example.naamadrory.utilities.BackgroundMusicPlayer
import com.example.naamadrory.utilities.SignalManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
        BackgroundMusicPlayer.init(this)
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.background)
    }

    override fun onTerminate() {
        super.onTerminate()
        BackgroundMusicPlayer.getInstance().stopMusic()
    }
}