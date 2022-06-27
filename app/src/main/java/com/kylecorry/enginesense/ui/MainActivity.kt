package com.kylecorry.enginesense.ui

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kylecorry.andromeda.core.system.Exceptions
import com.kylecorry.andromeda.core.tryOrNothing
import com.kylecorry.andromeda.fragments.AndromedaActivity
import com.kylecorry.enginesense.R
import java.time.Duration

class MainActivity : AndromedaActivity() {

    private lateinit var navController: NavController

    private val permissions = mutableListOf(
        Manifest.permission.BLUETOOTH
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        navController = findNavController()

        requestPermissions(permissions) {
            navController.navigate(R.id.action_codes)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        setIntent(intent)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        if (savedInstanceState.containsKey("navigation")) {
//            tryOrNothing {
//                val bundle = savedInstanceState.getBundle("navigation_arguments")
//                navController.navigate(savedInstanceState.getInt("navigation"), bundle)
//            }
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        navController.currentBackStackEntry?.arguments?.let {
//            outState.putBundle("navigation_arguments", it)
//        }
//        navController.currentDestination?.id?.let {
//            outState.putInt("navigation", it)
//        }
//    }

    private fun findNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.fragment_holder) as NavHostFragment).navController
    }
}
