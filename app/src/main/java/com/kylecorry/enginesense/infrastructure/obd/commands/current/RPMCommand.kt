package com.kylecorry.enginesense.infrastructure.obd.commands.current

import com.kylecorry.enginesense.infrastructure.obd.commands.HexCommand

class RPMCommand: HexCommand() {
    override val command: String = "01 0C"

    override fun parse(response: ByteArray): String {
        val a = response[2].toInt()
        val b = response[3].toInt()
        return ((a * 256 + b) / 4).toString()
    }
}