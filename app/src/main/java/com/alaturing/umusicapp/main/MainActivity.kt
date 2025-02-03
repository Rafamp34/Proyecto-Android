package com.alaturing.umusicapp.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alaturing.umusicapp.R
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import com.alaturing.umusicapp.authentication.ui.AuthenticationActivity
import com.alaturing.umusicapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navViewModel: NavigationSharedViewModel

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUI()
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

        navViewModel = ViewModelProvider(this)[NavigationSharedViewModel::class.java]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navViewModel.navigationEvents.collect { event ->
                    when (event) {
                        is NavigationEvent.ToIncidents -> {
                            navController.navigate(R.id.home,null,
                                NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build())
                        }
                        is NavigationEvent.ToHome -> {
                        }
                    }
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideNavbar = destination.arguments["hideNavbar"]
            binding.mainBottomNav.isVisible = true
            hideNavbar?.let {
                binding.mainBottomNav.isVisible = false
            }
        }
    }
}