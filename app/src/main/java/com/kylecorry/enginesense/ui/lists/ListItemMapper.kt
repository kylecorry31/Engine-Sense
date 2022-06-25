package com.kylecorry.enginesense.ui.lists

interface ListItemMapper<T> {
    fun map(value: T): ListItem
}