package com.kylecorry.enginesense.infrastructure.obd.commands.trouble

import com.kylecorry.enginesense.infrastructure.obd.commands.OBDCommand

abstract class TroubleCodesCommand(final override val command: String) : OBDCommand {

    private val header = "4${command[1]}"

    private val codeReplacement = mapOf(
        '0' to "P0",
        '1' to "P1",
        '2' to "P2",
        '3' to "P3",
        '4' to "C0",
        '5' to "C1",
        '6' to "C2",
        '7' to "C3",
        '8' to "B0",
        '9' to "B1",
        'A' to "B2",
        'B' to "B3",
        'C' to "U0",
        'D' to "U1",
        'E' to "U2",
        'F' to "U3"
    )

    override fun parse(response: String): String {
        // Remove the PID
        val data = mutableListOf<String>()

        // TODO: Figure out why the response is repeated
        for (frame in response.trim().split("\r")) {
            val frameData = frame.replace(" ", "")
            val canOneFrame = frameData.replace(Regex("[\r\n]"), "")
            val canOneFrameLength = canOneFrame.length
            data += if (canOneFrameLength <= 16 && canOneFrameLength % 4 == 0) {
                //CAN(ISO-15765) protocol one frame.
                frameData.substring(4) //47yy{codes} Header is 47yy, yy showing the number of data items.
            } else if (frameData.contains(":")) {
                //CAN(ISO-15765) protocol two and more frames.
                frameData.replace(Regex("[\r\n].:"), "")
                    .substring(7) //xxx47yy{codes}, Header is xxx47yy, xxx is bytes of information to follow, yy showing the number of data items.
            } else { //ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
                frameData.replace(Regex("^$header|[\r\n]$header|[\r\n]"), "")
            }
        }

        // Parse the codes (grouping 4 characters at a time)
        val codes = mutableListOf<String>()
        for (value in data) {
            if (value.length > 4) {
                codes.addAll(value.chunked(4).map { parseCode(it) })
            }
        }

        return codes.toSet().filterNot { it == "P0000" }.joinToString("\n")
    }

    private fun parseCode(code: String): String {
        var dtc = codeReplacement[code[0]] ?: throw IllegalArgumentException("Invalid code: $code")
        dtc += code.substring(1)
        return dtc
    }

}