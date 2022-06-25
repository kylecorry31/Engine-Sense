package com.kylecorry.enginesense.ui

import com.kylecorry.andromeda.bluetooth.IBluetoothDevice

interface ObdConnectionListener {
    fun onConnected(device: IBluetoothDevice)
    fun onDisconnected()
}