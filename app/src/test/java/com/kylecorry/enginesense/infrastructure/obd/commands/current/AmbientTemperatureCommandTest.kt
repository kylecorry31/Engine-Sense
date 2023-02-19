package com.kylecorry.enginesense.infrastructure.obd.commands.current

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AmbientTemperatureCommandTest {

    @Test
    fun getTemperature() {
        val command = AmbientTemperatureCommand()
        val result = command.parse("414629")
        assertEquals("1", result)
    }
}