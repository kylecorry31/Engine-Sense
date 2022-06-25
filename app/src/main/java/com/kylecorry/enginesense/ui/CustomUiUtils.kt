package com.kylecorry.enginesense.ui

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.kylecorry.andromeda.core.system.Resources

object CustomUiUtils {

    fun setImageColor(view: ImageView, @ColorInt color: Int?) {
        if (color == null) {
            view.clearColorFilter()
            return
        }
        view.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setImageColor(drawable: Drawable, @ColorInt color: Int?) {
        if (color == null) {
            drawable.clearColorFilter()
            return
        }
        drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setImageColor(textView: TextView, @ColorInt color: Int?) {
        textView.compoundDrawables.forEach {
            it?.let { setImageColor(it, color) }
        }
    }

    fun TextView.setCompoundDrawables(
        size: Int? = null,
        @DrawableRes left: Int? = null,
        @DrawableRes top: Int? = null,
        @DrawableRes right: Int? = null,
        @DrawableRes bottom: Int? = null
    ) {
        val leftDrawable = if (left == null) null else Resources.drawable(context, left)
        val rightDrawable = if (right == null) null else Resources.drawable(context, right)
        val topDrawable = if (top == null) null else Resources.drawable(context, top)
        val bottomDrawable = if (bottom == null) null else Resources.drawable(context, bottom)

        leftDrawable?.setBounds(
            0,
            0,
            size ?: leftDrawable.intrinsicWidth,
            size ?: leftDrawable.intrinsicHeight
        )
        rightDrawable?.setBounds(
            0,
            0,
            size ?: rightDrawable.intrinsicWidth,
            size ?: rightDrawable.intrinsicHeight
        )
        topDrawable?.setBounds(
            0,
            0,
            size ?: topDrawable.intrinsicWidth,
            size ?: topDrawable.intrinsicHeight
        )
        bottomDrawable?.setBounds(
            0,
            0,
            size ?: bottomDrawable.intrinsicWidth,
            size ?: bottomDrawable.intrinsicHeight
        )

        setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottomDrawable)

    }
}