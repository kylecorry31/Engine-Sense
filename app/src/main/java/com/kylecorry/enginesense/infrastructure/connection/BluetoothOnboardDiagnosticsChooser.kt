package com.kylecorry.enginesense.infrastructure.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.kylecorry.andromeda.alerts.Alerts
import com.kylecorry.andromeda.bluetooth.BluetoothService
import com.kylecorry.andromeda.pickers.Pickers
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.infrastructure.device.BluetoothOnboardDiagnostics
import com.kylecorry.enginesense.infrastructure.device.IOnboardDiagnostics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BluetoothOnboardDiagnosticsChooser(private val context: Context) :
    IOnboardDiagnosticsChooser {

    private val bluetooth = BluetoothService(context)

    override suspend fun getOBD(): IOnboardDiagnostics {
        val existingAddress: String? = null // TODO: Load from settings
        val addr = existingAddress ?: getDevice()?.address
        if (addr == null) {
            return withContext(Dispatchers.Main) {
                Alerts.toast(context, context.getString(R.string.unable_to_connect))
                getOBD()
            }
        }
        return try {
            BluetoothOnboardDiagnostics(bluetooth.getSecureDevice(addr))
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Alerts.toast(context, context.getString(R.string.unable_to_connect))
                getOBD()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getDevice(): BluetoothDevice? {

        val devices = withContext(Dispatchers.IO) {
            bluetooth.bondedDevices
//            val sensor = BluetoothScanner(context)
//            sensor.read()
//            sensor.devices
        }
        return withContext(Dispatchers.Main) {
            suspendCoroutine { cont ->
                Pickers.item(
                    context,
                    context.getString(R.string.device),
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


}