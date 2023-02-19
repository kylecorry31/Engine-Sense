package com.kylecorry.enginesense.infrastructure.obd.commands.current

import com.kylecorry.enginesense.infrastructure.obd.commands.HexCommand

class AmbientTemperatureCommand: HexCommand() {
    override val command: String = "01 46"

    override fun parse(response: ByteArray): String {
        return (response[2].toInt() - 40).toString()
    }
}