package com.kylecorry.enginesense.infrastructure.obd.commands.elm

import com.kylecorry.enginesense.infrastructure.obd.commands.RawCommand

class SetSpacesCommand(on: Boolean) : RawCommand("AT S${if (on) 1 else 0}")