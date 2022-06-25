package com.kylecorry.enginesense.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.fuel.FuelLevelCommand
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand
import com.github.pires.obd.commands.fuel.FuelTrimCommand
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand
import com.github.pires.obd.commands.fuel.WidebandAirFuelRatioCommand
import com.github.pires.obd.commands.engine.RPMCommand
import com.github.pires.obd.commands.engine.LoadCommand
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand
import com.github.pires.obd.commands.engine.OilTempCommand
import com.github.pires.obd.commands.engine.MassAirFlowCommand
import com.github.pires.obd.commands.engine.ThrottlePositionCommand
import com.github.pires.obd.commands.engine.RuntimeCommand
import com.github.pires.obd.commands.control.EquivalentRatioCommand
import com.github.pires.obd.commands.control.VinCommand
import com.github.pires.obd.commands.control.ModuleVoltageCommand
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand
import com.github.pires.obd.commands.pressure.FuelPressureCommand
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand
import com.github.pires.obd.commands.pressure.FuelRailPressureCommand
import com.github.pires.obd.commands.pressure.BarometricPressureCommand
import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.kylecorry.andromeda.core.time.Timer
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.enginesense.databinding.FragmentInfoBinding
import com.kylecorry.enginesense.infrastructure.bluetooth.ObdService
import com.kylecorry.enginesense.infrastructure.bluetooth.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InfoFragment : BoundFragment<FragmentInfoBinding>(), ObdConnectionListener {

    private var device: IBluetoothDevice? = null
    private val timer = Timer {
        scan()
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding {
        return FragmentInfoBinding.inflate(layoutInflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        ObdService.addListener(this)
        timer.interval(10000)
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

            val info = listOf(
                "Fuel pressure" to getValue(FuelPressureCommand()),
                "Fuel rail pressure" to getValue(FuelRailPressureCommand()),
                "Fuel level" to getPercent(FuelLevelCommand()),
                "Fuel ratio" to getValue(AirFuelRatioCommand(), "-"),
                "Fuel consumption" to getValue(ConsumptionRateCommand(), "-"),
                "Fuel type" to getValue(FindFuelTypeCommand(), "-"),
                "Fuel trim" to getValue(FuelTrimCommand()),
                "Fuel wideband ratio" to getValue(WidebandAirFuelRatioCommand(), "-"),
                "RPM" to getValue(RPMCommand()),
                "Load" to getValue(LoadCommand()),
                "Abs Load" to getValue(AbsoluteLoadCommand()),
                "Oil temp" to getValue(OilTempCommand()),
                "Mass air flow" to getValue(MassAirFlowCommand()),
                "Throttle" to getValue(ThrottlePositionCommand()),
                "Runtime" to getValue(RuntimeCommand()),
                "Equivalent Ratio" to getValue(EquivalentRatioCommand()),
                "VIN" to getValue(VinCommand()),
                "Voltage" to getValue(ModuleVoltageCommand()),
                "Ambient temp" to getValue(AmbientAirTemperatureCommand()),
                "Coolant temp" to getValue(EngineCoolantTemperatureCommand()),
                "Air intake temp" to getValue(AirIntakeTemperatureCommand()),
                "Intake manifold pressure" to getValue(IntakeManifoldPressureCommand()),
                "Barometric pressure" to getValue(BarometricPressureCommand())
            )

            if (isBound) {
                withContext(Dispatchers.Main) {
                    binding.info.text = buildSpannedString {
                        info.forEach {
                            bold {
                                append("${it.first}: ")
                            }
                            append(it.second)
                            appendLine()
                        }

                    }
                }
            }
        }

    }

    private suspend fun getPercent(command: ObdCommand): String = withContext(Dispatchers.IO) {
        getValue(command, "0") + "%"
    }

    private suspend fun getValue(command: ObdCommand, default: String = "-"): String =
        withContext(Dispatchers.IO) {
            try {
                val device = device ?: return@withContext default
                device.execute(command) ?: default
            } catch (e: Exception) {
                default
            }
        }
}