package com.example.yourstory

import android.content.res.Configuration
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
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var hostFragment: Fragment
    private lateinit var hostFramentNavController: NavController

    private var fabClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        toolbar = binding.toolbar

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        toolbar.inflateMenu(R.menu.toolbar_settings_menu)

        bottomNavigationView  = binding.bottomNavigation;

        hostFramentNavController = findNavController(R.id.host_fragment)

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
            hostFramentNavController.navigate(R.id.thought_dialog)
        }
        binding.likertFab.setOnClickListener {
            hostFramentNavController.navigate(R.id.likertDialog)
        }
        setupActionBarWithNavController(hostFramentNavController, appBarConfiguration)
        bottomNavigationView.setupWithNavController(hostFramentNavController)

        setupActionBarWithNavController(hostFramentNavController,appBarConfiguration)
        bottomNavigationView.setupWithNavController(hostFramentNavController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_settings_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
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
            // User chose the "Favorite" action, mark the current item
            // as a favorite...
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