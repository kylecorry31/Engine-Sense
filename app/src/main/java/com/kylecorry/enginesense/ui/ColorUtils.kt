package com.kylecorry.enginesense.ui

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

object ColorUtils {

    @ColorInt
    fun mostContrastingColor(
        @ColorInt foreground1: Int,
        @ColorInt foreground2: Int,
        @ColorInt background: Int
    ): Int {
        // From https://newbedev.com/how-to-programmatically-calculate-the-contrast-ratio-between-two-colors
        val f1 = (299 * Color.red(foreground1) + 587 * Color.green(foreground1) + 114 * Color.blue(
            foreground1
        )) / 1000f

        val f2 = (299 * Color.red(foreground2) + 587 * Color.green(foreground2) + 114 * Color.blue(
            foreground2
        )) / 1000f

        val b = (299 * Color.red(background) + 587 * Color.green(background) + 114 * Color.blue(
            background
        )) / 1000f

        val r1 = f1 - b
        val r2 = f2 - b

        return if (r1.absoluteValue > r2.absoluteValue) foreground1 else foreground2
    }

}