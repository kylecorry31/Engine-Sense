package com.kylecorry.enginesense.infrastructure.obd.commands.trouble

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class PermanentTroubleCodesCommandTest {

    @ParameterizedTest
    @MethodSource("provideCodes")
    fun getTroubleCodes(response: String, expected: String) {
        val command = PermanentTroubleCodesCommand()
        val result = command.parse(response)
        assertEquals(expected, result)
    }

    companion object {

        @JvmStatic
        fun provideCodes(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("4A 01 33 00 00 00 00", "P0133"),
                Arguments.of("4A 01 33 03 01 00 00", "P0133\nP0301"),
                Arguments.of("4A 00 \r4A 00", ""),
            )
        }

    }

}