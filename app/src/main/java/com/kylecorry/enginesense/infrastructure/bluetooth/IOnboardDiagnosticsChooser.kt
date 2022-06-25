package com.kylecorry.enginesense.infrastructure.bluetooth

interface IOnboardDiagnosticsChooser {
    suspend fun getOBD(): IOnboardDiagnostics
}