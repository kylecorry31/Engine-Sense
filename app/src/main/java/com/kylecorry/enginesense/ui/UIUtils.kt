package com.kylecorry.enginesense.ui

import android.content.res.ColorStateList
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.ColorInt
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.core.ui.Colors
import com.kylecorry.enginesense.R

object UIUtils {
    fun setButtonState(button: ImageButton, state: Boolean) {
        setButtonState(
            button,
            state,
            Resources.getAndroidColorAttr(button.context, R.attr.colorPrimary),
            Resources.color(button.context, R.color.colorSecondary)
        )
    }

    private fun setButtonState(
        button: ImageButton,
        isOn: Boolean,
        @ColorInt primaryColor: Int,
        @ColorInt secondaryColor: Int
    ) {
        if (isOn) {
            button.drawable?.let { Colors.setImageColor(it, secondaryColor) }
            button.backgroundTintList = ColorStateList.valueOf(primaryColor)
        } else {
            button.drawable?.let {
                Colors.setImageColor(
                    it,
                    Resources.androidTextColorSecondary(button.context)
                )
            }
            button.backgroundTintList =
                ColorStateList.valueOf(Resources.androidBackgroundColorSecondary(button.context))
        }
    }


    fun setButtonState(
        button: Button,
        isOn: Boolean
    ) {
        if (isOn) {
            button.setTextColor(
                Resources.color(button.context, R.color.colorSecondary)
            )
            button.backgroundTintList = ColorStateList.valueOf(
                Resources.getAndroidColorAttr(button.context, R.attr.colorPrimary)
            )
        } else {
            button.setTextColor(Resources.androidTextColorSecondary(button.context))
            button.backgroundTintList =
                ColorStateList.valueOf(Resources.androidBackgroundColorSecondary(button.context))
        }
    }
}