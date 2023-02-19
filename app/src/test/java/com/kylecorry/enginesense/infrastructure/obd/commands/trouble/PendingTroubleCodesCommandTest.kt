package com.kylecorry.enginesense.infrastructure.obd.commands.trouble

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PendingTroubleCodesCommandTest {

    @Test
    fun getTroubleCodes() {
        val command = PendingTroubleCodesCommand()
        val result = command.parse("4700\r4700")
        assertEquals("", result)
    }

}