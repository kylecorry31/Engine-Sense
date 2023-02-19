package com.kylecorry.enginesense.infrastructure.obd.commands.elm

import com.kylecorry.enginesense.infrastructure.obd.commands.RawCommand

class SetHeadersCommand(on: Boolean) : RawCommand("AT H${if (on) 1 else 0}")