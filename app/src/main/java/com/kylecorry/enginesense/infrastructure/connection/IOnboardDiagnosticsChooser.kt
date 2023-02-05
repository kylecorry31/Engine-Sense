package com.kylecorry.enginesense.infrastructure.connection

import com.kylecorry.enginesense.infrastructure.device.IOnboardDiagnostics

interface IOnboardDiagnosticsChooser {
    suspend fun getOBD(): IOnboardDiagnostics
}