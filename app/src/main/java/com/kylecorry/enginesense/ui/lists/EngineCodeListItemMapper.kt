package com.kylecorry.enginesense.ui.lists

import android.content.Context
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.domain.EngineCode
import com.kylecorry.enginesense.domain.EngineCodeStatus

class EngineCodeListItemMapper(
    private val context: Context,
    private val onClick: (EngineCode) -> Unit
) : ListItemMapper<EngineCode> {
    override fun map(value: EngineCode): ListItem {
        return ListItem(
            value.code.hashCode().toLong(),
            value.code.uppercase(),
            getStatusText(value.status),
            // TODO: Color based on severity?
            icon = ResourceListIcon(R.drawable.engine, Resources.androidTextColorSecondary(context))
        ) {
            onClick(value)
        }
    }

    private fun getStatusText(status: EngineCodeStatus): String {
        return when (status) {
            EngineCodeStatus.Confirmed -> context.getString(R.string.code_status_confirmed)
            EngineCodeStatus.Permanent -> context.getString(R.string.code_status_permanent)
            EngineCodeStatus.Pending -> context.getString(R.string.code_status_pending)
        }
    }

}