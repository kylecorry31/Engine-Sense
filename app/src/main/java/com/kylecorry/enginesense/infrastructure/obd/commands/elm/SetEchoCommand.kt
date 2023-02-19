package com.kylecorry.enginesense.infrastructure.obd.commands.elm

import com.kylecorry.enginesense.infrastructure.obd.commands.RawCommand

class SetEchoCommand(on: Boolean) : RawCommand("AT E${if (on) 1 else 0}")