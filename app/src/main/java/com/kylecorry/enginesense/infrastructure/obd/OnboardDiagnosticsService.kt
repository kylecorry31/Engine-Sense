package com.kylecorry.enginesense.infrastructure.obd

import android.util.Log
import com.kylecorry.andromeda.core.io.readUntil
import com.kylecorry.andromeda.core.io.write
import com.kylecorry.enginesense.infrastructure.obd.commands.OBDCommand
import kotlinx.coroutines.delay
import java.io.InputStream
import java.io.OutputStream
import java.time.Duration

class OnboardDiagnosticsService(
    private val input: InputStream,
    private val output: OutputStream
) {

    suspend fun execute(command: String, responseDelay: Duration? = null): String {
        val fullCommand = if (command.endsWith('\r')) command else "$command\r"

        // Send
        output.write(fullCommand)

        // Wait
        responseDelay?.let { delay(it.toMillis()) }

        // Read
        var response = input.readUntil('>')

        Log.d(javaClass.simpleName, "${command.trim()}: ${response.trim()}")

        // Remove bus messages
        response = response.replace("BUSINIT", "")
        response = response.replace("SEARCHING...", "")
        response = response.replace("SEARCHING", "")

        return response
    }

    suspend fun execute(command: OBDCommand): String {
        return command.parse(execute(command.command))
    }

}