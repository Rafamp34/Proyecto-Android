package com.alaturing.umusicapp.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clase que gestiona la inicialización y proporciona instancias de los servicios de Firebase
 */
@Singleton
class FirebaseManager @Inject constructor() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    fun initialize(context: Context) {
        FirebaseApp.initializeApp(context)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Inicializar Firestore con configuración optimizada
        firestore = Firebase.firestore
        val settings = firestoreSettings {
            isPersistenceEnabled = true
            cacheSizeBytes = FirebaseFirestore.CACHE_SIZE_UNLIMITED
        }
        firestore.firestoreSettings = settings

        // Inicializar Firebase Storage
        storage = Firebase.storage
    }

    // Getters para las instancias de Firebase
    fun getAuth(): FirebaseAuth = auth

    fun getFirestore(): FirebaseFirestore = firestore

    fun getStorage(): FirebaseStorage = storage

    // Colecciones de Firestore
    companion object {
        const val USERS_COLLECTION = "users"
        const val SONGS_COLLECTION = "songs"
        const val PLAYLISTS_COLLECTION = "playlists"
        const val ARTISTS_COLLECTION = "artists"
    }
}