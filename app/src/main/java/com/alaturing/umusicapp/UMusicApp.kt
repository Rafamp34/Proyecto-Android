package com.alaturing.umusicapp

import android.app.Application
import com.alaturing.umusicapp.firebase.FirebaseManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UMusicApp : Application() {

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase
        firebaseManager.initialize(this)
    }
}