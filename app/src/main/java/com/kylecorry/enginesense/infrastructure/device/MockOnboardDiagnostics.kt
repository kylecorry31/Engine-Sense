package com.kylecorry.enginesense.infrastructure.device

import com.kylecorry.enginesense.domain.DiagnosticTroubleCode

class MockOnboardDiagnostics(private val vin: String, private val troubleCodes: List<DiagnosticTroubleCode>) :
    IOnboardDiagnostics {

    private var isConnected = false

    override fun isConnected(): Boolean {
        return isConnected
    }

    override suspend fun connect() {
        isConnected = true
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override suspend fun getTroubleCodes(): List<DiagnosticTroubleCode> {
        return troubleCodes
    }

    override suspend fun clearTroubleCodes() {
    }

    override suspend fun getVIN(): String {
        return vin
    }
}