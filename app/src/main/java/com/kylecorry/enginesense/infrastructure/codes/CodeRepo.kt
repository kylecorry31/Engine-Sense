package com.kylecorry.enginesense.infrastructure.codes

import android.content.Context
import com.kylecorry.andromeda.json.JsonConvert

class CodeRepo(context: Context) {

    private val codes by lazy {
        val text = context.assets.open("codes.json").bufferedReader().readText()
        JsonConvert.fromJson<Codes>(text)
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