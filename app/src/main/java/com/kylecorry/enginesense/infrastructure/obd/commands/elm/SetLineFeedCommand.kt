package com.kylecorry.enginesense.infrastructure.obd.commands.elm

import com.kylecorry.enginesense.infrastructure.obd.commands.RawCommand

class SetLineFeedCommand(on: Boolean) : RawCommand("AT L${if (on) 1 else 0}")