package com.example.naamadrory

import android.app.Application
import com.example.naamadrory.utilities.SignalManager
import com.example.naamadrory.utilities.ImageLoader

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)
        SignalManager.init(this)
    }
}