package com.kylecorry.enginesense.infrastructure.device

import android.util.Log
import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.domain.DiagnosticTroubleCodeStatus
import com.kylecorry.enginesense.infrastructure.obd.OnboardDiagnosticsService
import com.kylecorry.enginesense.infrastructure.obd.commands.*
import com.kylecorry.enginesense.infrastructure.obd.commands.current.*
import com.kylecorry.enginesense.infrastructure.obd.commands.trouble.*
import com.kylecorry.enginesense.infrastructure.obd.commands.elm.*
import com.kylecorry.enginesense.infrastructure.obd.commands.info.VinCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BluetoothOnboardDiagnostics(private val device: IBluetoothDevice) : IOnboardDiagnostics {

    private var isConnected = false

    override fun isConnected(): Boolean {
        return isConnected && device.isConnected()
    }

    override suspend fun connect() = withContext(Dispatchers.IO) {
        if (isConnected()) {
            return@withContext
        }
        device.connect()

        isConnected = true

        try {
            val commands = listOf(
                SetDefaultsCommand(),
                ResetCommand(),
                SetEchoCommand(false),
                SetLineFeedCommand(false),
                SetSpacesCommand(false),
                SetHeadersCommand(false),
//                SetAllowLongMessages(),
//                TimeoutCommand(125),
                SelectProtocolCommand(0),
                RawCommand("01 01"),
                RPMCommand() // Read something to clear the bus
            )
            commands.forEach { execute(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            isConnected = false
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            isConnected = false
            return@withContext
        }
        isConnected = false
        device.disconnect()
    }

    override suspend fun getTroubleCodes(): List<DiagnosticTroubleCode> {
        val pending = getCodes(PendingTroubleCodesCommand())
        val permanent = getCodes(PermanentTroubleCodesCommand())
        val confirmed = getCodes(StoredTroubleCodesCommand())

        return confirmed.map { DiagnosticTroubleCode(it, DiagnosticTroubleCodeStatus.Confirmed) } +
                permanent.map { DiagnosticTroubleCode(it, DiagnosticTroubleCodeStatus.Permanent) } +
                pending.map { DiagnosticTroubleCode(it, DiagnosticTroubleCodeStatus.Pending) }
    }

    override suspend fun clearTroubleCodes() {
        execute("04")
    }

    override suspend fun getVIN(): String {
        return execute(VinCommand()) ?: ""
    }

    private suspend fun execute(command: String): String? = withContext(Dispatchers.IO) {
        execute(RawCommand(command))
    }

    private suspend fun execute(command: OBDCommand): String? = withContext(Dispatchers.IO) {
        if (!isConnected()) {
            return@withContext null
        }
        val input = device.getInputStream() ?: return@withContext null
        val output = device.getOutputStream() ?: return@withContext null
        val reader = OnboardDiagnosticsService(input, output)
        val result = reader.execute(command)
        Log.d(javaClass.simpleName, "${command.command}: $result")
        result
    }

    private suspend fun getCodes(command: OBDCommand): List<String> = withContext(Dispatchers.IO) {
        execute(command)?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
    }

}