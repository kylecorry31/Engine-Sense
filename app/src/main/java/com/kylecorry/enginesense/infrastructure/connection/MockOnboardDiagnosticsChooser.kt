package com.kylecorry.enginesense.infrastructure.connection

import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.domain.DiagnosticTroubleCodeStatus
import com.kylecorry.enginesense.infrastructure.device.IOnboardDiagnostics
import com.kylecorry.enginesense.infrastructure.device.MockOnboardDiagnostics

class MockOnboardDiagnosticsChooser : IOnboardDiagnosticsChooser {
    override suspend fun getOBD(): IOnboardDiagnostics {
        return MockOnboardDiagnostics(
            "1234",
            listOf(
                DiagnosticTroubleCode("P0100", DiagnosticTroubleCodeStatus.Confirmed),
                DiagnosticTroubleCode("P18D4", DiagnosticTroubleCodeStatus.Confirmed),
                DiagnosticTroubleCode("C0301", DiagnosticTroubleCodeStatus.Confirmed),
                DiagnosticTroubleCode("C0700", DiagnosticTroubleCodeStatus.Confirmed),
                DiagnosticTroubleCode("C0A00", DiagnosticTroubleCodeStatus.Confirmed),
            )
        )
    }
}