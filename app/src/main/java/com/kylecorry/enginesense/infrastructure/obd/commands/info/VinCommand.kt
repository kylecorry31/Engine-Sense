package com.kylecorry.enginesense.infrastructure.obd.commands.info

import com.kylecorry.enginesense.infrastructure.obd.commands.OBDCommand
import java.util.regex.Pattern

class VinCommand: OBDCommand {
    override val command: String = "09 02"

    override fun parse(response: String): String {
        val filtered = response.replace(Regex("\\s"), "")
        var workingData: String
        if (filtered.contains(":")) { //CAN(ISO-15765) protocol.
            workingData = filtered.replace(".:".toRegex(), "")
                .substring(9) //9 is xxx490201, xxx is bytes of information to follow.
            val m = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
                .matcher(convertHexToString(workingData))
            if (m.find()) workingData =
                filtered.replace("0:49".toRegex(), "").replace(".:".toRegex(), "")
        } else { //ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = filtered.replace("49020.".toRegex(), "")
        }
        return convertHexToString(workingData).replace("[\u0000-\u001f]".toRegex(), "")
    }

    private fun convertHexToString(hex: String): String {
        val sb = StringBuilder()
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        var i = 0
        while (i < hex.length - 1) {


            //grab the hex in pairs
            val output = hex.substring(i, i + 2)
            //convert hex to decimal
            val decimal = output.toInt(16)
            //convert the decimal to character
            sb.append(decimal.toChar())
            i += 2
        }
        return sb.toString()
    }
}