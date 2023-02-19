package com.kylecorry.enginesense.infrastructure.obd.commands.current

import com.kylecorry.enginesense.infrastructure.obd.commands.HexCommand

class ThrottlePositionCommand: HexCommand() {
    override val command: String = "01 0D"

    override fun parse(response: ByteArray): String {
        return (response[2].toInt() * 100 / 255.0).toInt().toString()
    }
}