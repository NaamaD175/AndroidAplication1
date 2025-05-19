package com.example.naamadrory

import android.app.Application
import com.example.naamadrory.utilities.SignalManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}