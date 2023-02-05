package com.kylecorry.enginesense.infrastructure.device

import com.kylecorry.enginesense.domain.DiagnosticTroubleCode

class MockOnboardDiagnostics(private val vin: String, private val troubleCodes: List<DiagnosticTroubleCode>) :
    IOnboardDiagnostics {
    override suspend fun connect() {
    }

    override suspend fun disconnect() {
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