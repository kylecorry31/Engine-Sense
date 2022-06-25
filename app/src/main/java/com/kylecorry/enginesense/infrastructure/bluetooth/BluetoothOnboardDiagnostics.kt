package com.kylecorry.enginesense.infrastructure.bluetooth

import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.control.PendingTroubleCodesCommand
import com.github.pires.obd.commands.control.PermanentTroubleCodesCommand
import com.github.pires.obd.commands.control.TroubleCodesCommand
import com.github.pires.obd.commands.control.VinCommand
import com.github.pires.obd.commands.protocol.EchoOffCommand
import com.github.pires.obd.commands.protocol.LineFeedOffCommand
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import com.github.pires.obd.enums.ObdProtocols
import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.kylecorry.enginesense.domain.TroubleCode
import com.kylecorry.enginesense.domain.TroubleCodeStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BluetoothOnboardDiagnostics(private val device: IBluetoothDevice) : IOnboardDiagnostics {

    private var isConnected = false

    override suspend fun connect() = withContext(Dispatchers.IO) {
        if (isConnected) {
            return@withContext
        }
        device.connect()
        val commands = listOf(
            EchoOffCommand(),
            LineFeedOffCommand(),
            TimeoutCommand(125),
            SelectProtocolCommand(ObdProtocols.AUTO)
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

    override suspend fun getTroubleCodes(): List<TroubleCode> {
        val pending = getCodes(PendingTroubleCodesCommand())
        val permanent = getCodes(PermanentTroubleCodesCommand())
        val confirmed = getCodes(TroubleCodesCommand())

        return confirmed.map { TroubleCode(it, TroubleCodeStatus.Confirmed) } +
                permanent.map { TroubleCode(it, TroubleCodeStatus.Permanent) } +
                pending.map { TroubleCode(it, TroubleCodeStatus.Pending) }
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