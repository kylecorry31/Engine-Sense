package com.kylecorry.enginesense.infrastructure.codes

import android.content.Context
import com.kylecorry.andromeda.json.JsonConvert

class CodeRepo(context: Context) {

    private val codes by lazy {
        val text = context.assets.open("codes.json").bufferedReader().readText()
        JsonConvert.fromJson<Codes>(text)
    }

    fun getSystem(code: String): String? {
        if (code.isEmpty()) {
            return null
        }
        return when (code[0]) {
            'P' -> "Powertrain"
            'C' -> "Chassis"
            'B' -> "Body"
            'U' -> "Network"
            else -> null
        }
    }

    fun isStandard(code: String): Boolean {
        return code.length >= 2 && code[1] == '0'
    }

    fun getCategory(code: String): String? {
        if (code.length < 3) {
            return null
        }
        return when (code[2]) {
            '0' -> "Fuel and Air Metering and Auxiliary Emission Controls"
            '1' -> "Fuel and Air Metering"
            '2' -> "Fuel and Air Metering (injector circuit)"
            '3' -> "Ignition systems or misfires"
            '4' -> "Auxiliary emission controls"
            '5' -> "Vehicle speed control & idle control systems"
            '6' -> "Computer & output circuit"
            '7', '8', '9' -> "Transmission"
            'A', 'B', 'C' -> "Hybrid drive"
            else -> null
        }
    }

    fun getName(code: String): String? {
        return codes?.codes?.getOrDefault(code.uppercase(), null)
    }

    companion object {
        // Thread safe instance
        private var instance: CodeRepo? = null

        fun getInstance(context: Context): CodeRepo {
            synchronized(CodeRepo::class) {
                if (instance == null) {
                    instance = CodeRepo(context)
                }
            }
            return instance!!
        }
    }

    private data class Codes(val codes: Map<String, String>)

}