package com.kylecorry.enginesense.infrastructure.obd.commands.trouble

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class StoredTroubleCodesCommandTest {

    @Test
    fun getTroubleCodes() {
        val command = StoredTroubleCodesCommand()
        val result = command.parse("4300\r4300")
        assertEquals("", result)
    }
}