package com.mohit.chatkit

import android.app.Application
import com.pusher.chatkit.CurrentUser

class AppController() : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        lateinit var currentUser: CurrentUser
    }
}
