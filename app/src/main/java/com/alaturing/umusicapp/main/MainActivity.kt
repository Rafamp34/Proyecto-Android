package com.alaturing.umusicapp.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.databinding.ActivityMainBinding
import com.alaturing.umusicapp.firebase.FirebaseManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
        setupFirebaseAnalytics()
    }

    private fun initializeUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_navigation_area) as NavHostFragment
        val navController = navHostFragment.navController
        binding.mainBottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideNavbar = destination.arguments["hideNavbar"]
            binding.mainBottomNav.isVisible = true
            hideNavbar?.let {
                binding.mainBottomNav.isVisible = false
            }

            // Registrar navegación en Analytics
            destination.label?.let { label ->
                Firebase.analytics.logEvent("screen_view") {
                    param("screen_name", label.toString())
                    param("screen_class", this@MainActivity.javaClass.simpleName)
                }
            }
        }
    }

    /**
     * Configura Firebase Analytics y Crashlytics
     */
    private fun setupFirebaseAnalytics() {
        // Habilitar/deshabilitar Crashlytics en desarrollo
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

        // Configurar el ID de usuario si está autenticado
        firebaseManager.getAuth().currentUser?.let { user ->
            Firebase.crashlytics.setUserId(user.uid)
            Firebase.analytics.setUserId(user.uid)
        }
    }
}