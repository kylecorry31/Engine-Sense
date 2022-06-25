package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.andromeda.bluetooth.IBluetoothDevice
import com.github.pires.obd.commands.ObdCommand
import com.github.pires.obd.commands.protocol.EchoOffCommand
import com.github.pires.obd.commands.protocol.LineFeedOffCommand
import com.github.pires.obd.commands.protocol.SelectProtocolCommand
import com.github.pires.obd.commands.protocol.TimeoutCommand
import com.github.pires.obd.enums.ObdProtocols
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun IBluetoothDevice.execute(command: ObdCommand): String? = withContext(Dispatchers.IO) {
    val input = getInputStream()
    val output = getOutputStream()
    command.run(input, output)
    command.calculatedResult
}

suspend fun IBluetoothDevice.initialize() {
    val commands = listOf(
        EchoOffCommand(),
        LineFeedOffCommand(),
        TimeoutCommand(125),
        SelectProtocolCommand(ObdProtocols.AUTO)
    )
    commands.forEach { execute(it) }
}