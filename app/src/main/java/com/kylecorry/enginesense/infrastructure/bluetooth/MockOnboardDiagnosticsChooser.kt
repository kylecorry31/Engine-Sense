package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.domain.DiagnosticTroubleCodeStatus

class MockOnboardDiagnosticsChooser : IOnboardDiagnosticsChooser {
    override suspend fun getOBD(): IOnboardDiagnostics {
        return MockOnboardDiagnostics(
            "1234",
            listOf(DiagnosticTroubleCode("P0100", DiagnosticTroubleCodeStatus.Confirmed))
        )
    }
}