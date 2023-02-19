package com.kylecorry.enginesense.infrastructure.obd.commands

open class RawCommand(override val command: String) : OBDCommand {
    override fun parse(response: String): String {
        return response
    }
}