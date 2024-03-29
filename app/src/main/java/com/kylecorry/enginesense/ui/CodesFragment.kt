package com.kylecorry.enginesense.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kylecorry.andromeda.alerts.Alerts
import com.kylecorry.andromeda.alerts.toast
import com.kylecorry.andromeda.clipboard.Clipboard
import com.kylecorry.andromeda.core.coroutines.onMain
import com.kylecorry.andromeda.core.time.Timer
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.databinding.FragmentCodesBinding
import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.infrastructure.connection.BluetoothOnboardDiagnosticsChooser
import com.kylecorry.enginesense.infrastructure.connection.MockOnboardDiagnosticsChooser
import com.kylecorry.enginesense.infrastructure.device.IOnboardDiagnostics
import com.kylecorry.enginesense.ui.lists.TroubleCodeListItemMapper
import kotlinx.coroutines.delay

class CodesFragment : BoundFragment<FragmentCodesBinding>() {

    private var device: IOnboardDiagnostics? = null
    private val mapper by lazy { TroubleCodeListItemMapper(requireContext()) }
    private val loading by lazy {
        val indicator = Alerts.loading(requireContext(), getString(R.string.connecting))
        indicator.hide()
        indicator
    }
    private val timer = Timer {
        inBackground {
            scan()
        }
    }

    private var codes: List<DiagnosticTroubleCode> = emptyList()

    private val useMock = false

    private val obdChooser by lazy {
        if (useMock) {
            MockOnboardDiagnosticsChooser()
        } else {
            BluetoothOnboardDiagnosticsChooser(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titlebar.leftButton.setOnClickListener {
            if (device?.isConnected() == true) {
                disconnect()
                clearDevice()
            } else {
                connect()
            }
        }

        binding.titlebar.rightButton.setOnClickListener {
            Clipboard.copy(
                requireContext(),
                codes.joinToString("\n") { it.code },
                getString(R.string.copied)
            )
        }
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCodesBinding {
        return FragmentCodesBinding.inflate(layoutInflater, container, false)
    }

    private fun connect() {
        inBackground {
            var failures = 0
            while (true) {
                try {
                    if (device == null) {
                        device = obdChooser.getOBD()
                    }
                    loading.show()
                    device?.connect()

                    // Force it into the catch block when it isn't connected
                    if (device?.isConnected() != true) {
                        throw Exception("Unable to connect")
                    }
                    loading.hide()
                    break
                } catch (e: Exception) {
                    failures++
                    if (failures > MAX_FAILURES) {
                        toast(getString(R.string.unable_to_connect))
                        e.printStackTrace()
                        clearDevice()
                        failures = 0
                    } else {
                        delay(RETRY_DURATION)
                    }
                    loading.hide()
                }
            }
            UIUtils.setButtonState(binding.titlebar.leftButton, true)
            scan()
        }
    }

    private fun clearDevice() {
        device = null
        // TODO: Clear saved device
    }

    private fun disconnect() {
        inBackground {
            try {
                device?.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            UIUtils.setButtonState(binding.titlebar.leftButton, false)
        }
    }

    override fun onResume() {
        super.onResume()
        connect()
    }

    override fun onPause() {
        super.onPause()
        timer.stop()
        disconnect()
    }

    private suspend fun scan() {
        if (device?.isConnected() == false) {
            toast(getString(R.string.disconnected))
            disconnect()
            return
        }

        try {
            codes = device?.getTroubleCodes() ?: emptyList()
            val vin = device?.getVIN() ?: ""
            onMain {
                binding.codes.setItems(codes, mapper)
                binding.titlebar.subtitle.text = vin
            }
        } catch (e: Exception) {
            toast(getString(R.string.disconnected))
            disconnect()
            return
        }

        timer.once(5000)
    }

    companion object {
        private const val MAX_FAILURES = 8
        private const val RETRY_DURATION = 2000L
    }
}