package com.kylecorry.enginesense.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kylecorry.andromeda.alerts.toast
import com.kylecorry.andromeda.core.time.Timer
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.databinding.FragmentCodesBinding
import com.kylecorry.enginesense.infrastructure.connection.BluetoothOnboardDiagnosticsChooser
import com.kylecorry.enginesense.infrastructure.device.IOnboardDiagnostics
import com.kylecorry.enginesense.ui.lists.TroubleCodeListItemMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CodesFragment : BoundFragment<FragmentCodesBinding>() {

    private var device: IOnboardDiagnostics? = null
    private val mapper by lazy { TroubleCodeListItemMapper(requireContext()) }
    private val timer = Timer {
        runInBackground {
            scan()
        }
    }
    private val obdChooser by lazy { BluetoothOnboardDiagnosticsChooser(requireContext()) }
//    private val obdChooser = MockOnboardDiagnosticsChooser()

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCodesBinding {
        return FragmentCodesBinding.inflate(layoutInflater, container, false)
    }

    private fun connect() {
        runInBackground {
            while (true) {
                try {
                    device = obdChooser.getOBD()
                    device?.connect()
                    break
                } catch (e: Exception) {
                    toast(getString(R.string.unable_to_connect))
                    e.printStackTrace()
                }
            }
            scan()
        }
    }

    private fun disconnect() {
        runInBackground {
            device?.disconnect()
            device = null
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

        if (isBound) {
            withContext(Dispatchers.Main) {
                binding.codes.setItems(codes, mapper)
            }
        }
        timer.once(5000)
    }
}