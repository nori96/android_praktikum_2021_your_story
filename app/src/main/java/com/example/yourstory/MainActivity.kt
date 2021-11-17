package com.example.yourstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yourstory.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var fabClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation;

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_reports, R.id.navigation_today, R.id.navigation_diary
            )
        )
        binding.rootFab.setOnClickListener {
            if (!fabClicked) {
                binding.likertFab.visibility = View.VISIBLE
                binding.thoughtFab.visibility = View.VISIBLE
            }
            else {
                binding.likertFab.visibility = View.INVISIBLE
                binding.thoughtFab.visibility = View.INVISIBLE
            }
            fabClicked = !fabClicked
        }
        binding.thoughtFab.setOnClickListener {
            navController.navigate(R.id.thought_dialog)
        }
        binding.likertFab.setOnClickListener {
            navController.navigate(R.id.likertDialog)
        }
        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}