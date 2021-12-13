package com.example.yourstory

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yourstory.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.nio.ByteBuffer
import java.util.*

class MainActivity : AppCompatActivity() {

    public lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var hostFramentNavController: NavController

    private var fabClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.inflateMenu(R.menu.toolbar_settings_menu)

        bottomNavigationView  = binding.bottomNavigation;

        hostFramentNavController = findNavController(R.id.host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_reports, R.id.navigation_today, R.id.navigation_diary
            )
        )

        setupActionBarWithNavController(hostFramentNavController,appBarConfiguration)
        bottomNavigationView.setupWithNavController(hostFramentNavController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_settings_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //TODO: Auslagerung des BackPressed-Vehaltens in die Navigation-Component
        16908332 -> {
            onBackPressed()
            true
        }
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            hostFramentNavController.navigate(R.id.settingsFragment)
            true
        }

        R.id.action_help -> {
            hostFramentNavController.navigate(R.id.helpFragment)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}