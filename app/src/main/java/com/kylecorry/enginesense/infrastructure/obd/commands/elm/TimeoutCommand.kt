package com.kylecorry.enginesense.infrastructure.obd.commands.elm

import com.kylecorry.enginesense.infrastructure.obd.commands.RawCommand

class TimeoutCommand(timeout: Int) : RawCommand("AT ST " + Integer.toHexString(0xFF and timeout))