package com.kylecorry.enginesense.infrastructure.device

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand
import com.github.pires.obd.commands.control.TroubleCodesCommand
import com.github.pires.obd.commands.control.VinCommand
import com.github.pires.obd.commands.protocol.*
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand
import com.github.pires.obd.enums.ObdProtocols
import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.domain.DiagnosticTroubleCodeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class BluetoothOnboardDiagnostics(private val device: IBluetoothDevice) : IOnboardDiagnostics {

    private var isConnected = false

    override fun isConnected(): Boolean {
        return isConnected
    }

    override suspend fun connect() = withContext(Dispatchers.IO) {
        if (isConnected) {
            return@withContext
        }
        device.connect()

        execute(ObdResetCommand())
        delay(500)

        val commands = listOf(
            EchoOffCommand(),
            LineFeedOffCommand(),
            TimeoutCommand(125),
            SelectProtocolCommand(ObdProtocols.AUTO),
            AmbientAirTemperatureCommand()  // Gets a reading to initialize
        )
        commands.forEach { execute(it) }
        isConnected = true
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        if (!isConnected) {
            return@withContext
        }
        isConnected = false
        device.disconnect()
    }

    override suspend fun getTroubleCodes(): List<DiagnosticTroubleCode> {
        val pending = getCodes(PendingTroubleCodesCommand())
        val permanent = getCodes(PermanentTroubleCodesCommand())
        val confirmed = getCodes(TroubleCodesCommand())

        return confirmed.map { DiagnosticTroubleCode(it, DiagnosticTroubleCodeStatus.Confirmed) } +
                permanent.map { DiagnosticTroubleCode(it, DiagnosticTroubleCodeStatus.Permanent) } +
                pending.map { DiagnosticTroubleCode(it, DiagnosticTroubleCodeStatus.Pending) }
    }

    override suspend fun clearTroubleCodes() {
        execute(ResetTroubleCodesCommand())
    }

    override suspend fun getVIN(): String {
        return execute(VinCommand()) ?: ""
    }

    private suspend fun execute(command: ObdCommand): String? = withContext(Dispatchers.IO) {
        val input = device.getInputStream()
        val output = device.getOutputStream()
        command.run(input, output)
        command.calculatedResult
    }

    private suspend fun getCodes(command: ObdCommand): List<String> = withContext(Dispatchers.IO) {
        execute(command)?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
    }

}