package com.kylecorry.enginesense.infrastructure.bluetooth

import com.kylecorry.enginesense.domain.TroubleCode
import com.kylecorry.enginesense.domain.TroubleCodeStatus

class MockOnboardDiagnosticsChooser : IOnboardDiagnosticsChooser {
    override suspend fun getOBD(): IOnboardDiagnostics {
        return MockOnboardDiagnostics(
            "1234",
            listOf(TroubleCode("P0100", TroubleCodeStatus.Confirmed))
        )
    }
}