package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.kylecorry.enginesense.ui.ObdConnectionListener

// TODO: Replace this with an event bus
object ObdService {
    var device: IBluetoothDevice? = null

    private val callbacks = mutableSetOf<ObdConnectionListener>()

    fun addListener(listener: ObdConnectionListener) {
        callbacks.add(listener)
        device.let {
            if (it == null) {
                listener.onDisconnected()
            } else {
                listener.onConnected(it)
            }
        }
    }

    fun removeListener(listener: ObdConnectionListener) {
        callbacks.remove(listener)
    }

    fun notifyListeners() {
        callbacks.forEach { listener ->
            device.let {
                if (it == null) {
                    listener.onDisconnected()
                } else {
                    listener.onConnected(it)
                }
            }
        }
    }

}