package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.enginesense.domain.TroubleCode

interface IOnboardDiagnostics {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun getTroubleCodes(): List<TroubleCode>
    suspend fun getVIN(): String
}