package com.kylecorry.enginesense.infrastructure.obd.commands.elm

import com.kylecorry.enginesense.infrastructure.obd.commands.RawCommand

class SelectProtocolCommand(protocol: Int): RawCommand("AT SP $protocol")