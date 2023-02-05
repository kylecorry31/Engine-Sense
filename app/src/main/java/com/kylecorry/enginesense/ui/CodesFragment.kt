package com.kylecorry.enginesense.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kylecorry.andromeda.alerts.toast
import com.kylecorry.andromeda.core.coroutines.onMain
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.core.time.Timer
import com.kylecorry.andromeda.core.ui.Colors
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.databinding.FragmentCodesBinding
import com.kylecorry.enginesense.infrastructure.connection.BluetoothOnboardDiagnosticsChooser
import com.kylecorry.enginesense.infrastructure.connection.MockOnboardDiagnosticsChooser
import com.kylecorry.enginesense.infrastructure.device.IOnboardDiagnostics
import com.kylecorry.enginesense.ui.lists.TroubleCodeListItemMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CodesFragment : BoundFragment<FragmentCodesBinding>() {

    private var device: IOnboardDiagnostics? = null
    private val mapper by lazy { TroubleCodeListItemMapper(requireContext()) }
    private val timer = Timer {
        inBackground {
            scan()
        }
    }

    //    private val obdChooser by lazy { BluetoothOnboardDiagnosticsChooser(requireContext()) }
    private val obdChooser = MockOnboardDiagnosticsChooser()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titlebar.leftButton.setOnClickListener {
            if (device?.isConnected() == true) {
                disconnect()
                device = null
                // TODO: Clear saved device
            } else {
                connect()
            }
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
            while (true) {
                try {
                    if (device == null) {
                        device = obdChooser.getOBD()
                    }
                    device?.connect()
                    break
                } catch (e: Exception) {
                    toast(getString(R.string.unable_to_connect))
                    e.printStackTrace()
                }
            }
            UIUtils.setButtonState(binding.titlebar.leftButton, true)
            scan()
        }
    }

    private fun disconnect() {
        inBackground {
            device?.disconnect()
            UIUtils.setButtonState(binding.titlebar.leftButton, false)
        }
    }

    @SuppressLint("MissingPermission")
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
        val codes = device?.getTroubleCodes() ?: emptyList()
        val vin = device?.getVIN() ?: ""
        onMain {
            binding.codes.setItems(codes, mapper)
            binding.titlebar.subtitle.text = vin
        }
        timer.once(5000)
    }
}