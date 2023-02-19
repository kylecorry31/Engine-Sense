package com.kylecorry.enginesense.infrastructure.obd.commands

abstract class HexCommand : OBDCommand {
    override fun parse(response: String): String {
        val filtered = response.replace(Regex("[.\\s]"), "")

        if (!filtered.matches(Regex("([0-9A-F])+"))) {
            throw IllegalArgumentException("Invalid response: $response")
        }

        val bytes = ByteArray(filtered.length / 2)

        for (i in filtered.indices step 2) {
            val str = filtered.substring(i, i + 2)
            bytes[i / 2] = str.toInt(16).toByte()
        }

        return parse(bytes)
    }

    protected abstract fun parse(response: ByteArray): String
}