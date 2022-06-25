package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.enginesense.domain.TroubleCode

class MockOnboardDiagnostics(private val vin: String, private val troubleCodes: List<TroubleCode>) :
    IOnboardDiagnostics {
    override suspend fun connect() {
    }

    override suspend fun disconnect() {
    }

    override suspend fun getTroubleCodes(): List<TroubleCode> {
        return troubleCodes
    }

    override suspend fun getVIN(): String {
        return vin
    }
}