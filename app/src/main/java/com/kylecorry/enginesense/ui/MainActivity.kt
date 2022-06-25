package com.kylecorry.enginesense.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kylecorry.andromeda.alerts.Alerts
import com.kylecorry.andromeda.bluetooth.BluetoothService
import com.kylecorry.andromeda.core.system.Exceptions
import com.kylecorry.andromeda.core.tryOrNothing
import com.kylecorry.andromeda.fragments.AndromedaActivity
import com.kylecorry.andromeda.pickers.Pickers
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.infrastructure.bluetooth.ObdService
import com.kylecorry.enginesense.infrastructure.bluetooth.initialize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AndromedaActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView

    private val bluetooth by lazy { BluetoothService(this) }

    private val permissions = mutableListOf(
        Manifest.permission.BLUETOOTH
    )

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Exceptions.onUncaughtException(Duration.ofMinutes(1)) {
            it.printStackTrace()
//            Alerts.dialog(
//                this@MainActivity,
//                getString(R.string.error_occurred),
//                getString(R.string.error_occurred_message),
//                okText = getString(R.string.pref_email_title)
//            ) { cancelled ->
//                if (cancelled) {
//                    exitProcess(2)
//                } else {
//                    ExceptionUtils.report(
//                        this@MainActivity,
//                        it,
//                        getString(R.string.email),
//                        getString(R.string.app_name)
//                    )
//                }
        }


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        navController = findNavController()
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        requestPermissions(permissions) {
            startApp()
        }
    }

    private fun startApp() {
        lifecycleScope.launch {
            val existingAddress: String? = null // TODO: Load from settings
            val addr = existingAddress ?: getDevice()?.address
            if (addr == null){
                withContext(Dispatchers.Main) {
                    Alerts.toast(this@MainActivity, getString(R.string.unable_to_connect))
                    // TODO: Clear last address
                    startApp()
                }
                return@launch
            }
            try {
                connect(addr)
                // TODO: Save address to settings
                ObdService.device?.initialize()
                ObdService.notifyListeners()
            } catch (e: Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Alerts.toast(this@MainActivity, getString(R.string.unable_to_connect))
                    // TODO: Clear last address
                    startApp()
                }
            }
        }
    }


    private suspend fun connect(address: String) = withContext(Dispatchers.IO) {
        println("Connecting to $address")
        ObdService.device = bluetooth.getSecureDevice(address)
        ObdService.device?.connect()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getDevice(): BluetoothDevice? {
        val devices = withContext(Dispatchers.IO) {
            bluetooth.devices
        }
        return withContext(Dispatchers.Main) {
            suspendCoroutine { cont ->
                Pickers.item(
                    this@MainActivity,
                    getString(R.string.device),
                    devices.map { it.name }) {
                    if (it != null) {
                        cont.resume(devices[it])
                    } else {
                        cont.resume(null)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        setIntent(intent)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomNavigation.selectedItemId = savedInstanceState.getInt(
            "page",
            R.id.action_codes
        )
        if (savedInstanceState.containsKey("navigation")) {
            tryOrNothing {
                val bundle = savedInstanceState.getBundle("navigation_arguments")
                navController.navigate(savedInstanceState.getInt("navigation"), bundle)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("page", bottomNavigation.selectedItemId)
        navController.currentBackStackEntry?.arguments?.let {
            outState.putBundle("navigation_arguments", it)
        }
        navController.currentDestination?.id?.let {
            outState.putInt("navigation", it)
        }
    }

    // TODO: Extract this to Andromeda
    fun getFragment(): Fragment? {
        return supportFragmentManager.fragments.firstOrNull()?.childFragmentManager?.fragments?.firstOrNull()
    }

    private fun findNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.fragment_holder) as NavHostFragment).navController
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        fun pendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(
                context,
                273235023,
                intent(context),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

}
