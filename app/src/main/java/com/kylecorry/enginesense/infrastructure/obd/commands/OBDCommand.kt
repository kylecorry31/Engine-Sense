package com.kylecorry.enginesense.infrastructure.obd.commands

interface OBDCommand {
    val command: String
    fun parse(response: String): String
}