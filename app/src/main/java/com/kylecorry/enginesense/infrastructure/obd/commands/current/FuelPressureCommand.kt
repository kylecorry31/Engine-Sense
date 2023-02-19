package com.kylecorry.enginesense.infrastructure.obd.commands.current

import com.kylecorry.enginesense.infrastructure.obd.commands.HexCommand

class FuelPressureCommand: HexCommand() {
    override val command: String = "01 0A"

    override fun parse(response: ByteArray): String {
        return (response[2].toInt() * 3).toString()
    }
}