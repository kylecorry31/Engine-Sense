package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.enginesense.domain.DiagnosticTroubleCode

interface IOnboardDiagnostics {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun getTroubleCodes(): List<DiagnosticTroubleCode>
    suspend fun clearTroubleCodes()
    suspend fun getVIN(): String
}