package com.kylecorry.enginesense.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand
import com.github.pires.obd.commands.control.TroubleCodesCommand
import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.kylecorry.andromeda.core.time.Timer
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.enginesense.databinding.FragmentCodesBinding
import com.kylecorry.enginesense.infrastructure.bluetooth.ObdService
import com.kylecorry.enginesense.infrastructure.bluetooth.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CodesFragment : BoundFragment<FragmentCodesBinding>(), ObdConnectionListener {

    private var device: IBluetoothDevice? = null
    private val timer = Timer {
        scan()
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCodesBinding {
        return FragmentCodesBinding.inflate(layoutInflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        ObdService.addListener(this)
        timer.interval(5000)
    }

    override fun onPause() {
        super.onPause()
        ObdService.removeListener(this)
        timer.stop()
    }

    override fun onConnected(device: IBluetoothDevice) {
        this.device = device
        scan()
    }

    override fun onDisconnected() {
        this.device = null
        scan()
    }

    private fun scan() {
        runInBackground {
            val pending = getCodes(PendingTroubleCodesCommand())
            val permanent = getCodes(PermanentTroubleCodesCommand())
            val confirmed = getCodes(TroubleCodesCommand())

            if (isBound) {
                withContext(Dispatchers.Main) {
                    binding.codes.text = buildSpannedString {
                        bold {
                            append("Confirmed\n")
                        }
                        confirmed.forEach { append(it + "\n") }
                        appendLine()

                        bold {
                            append("Permanent\n")
                        }
                        permanent.forEach { append(it + "\n") }
                        appendLine()

                        bold {
                            append("Pending\n")
                        }
                        pending.forEach { append(it + "\n") }
                    }
                }
            }
        }

    }

    private suspend fun getCodes(command: ObdCommand): List<String> = withContext(Dispatchers.IO) {
        val device = device ?: return@withContext emptyList()
        device.execute(command)?.split("\n") ?: emptyList()
    }
}