package com.kylecorry.enginesense.ui.lists

import android.content.Context
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.domain.TroubleCode
import com.kylecorry.enginesense.domain.TroubleCodeStatus

class TroubleCodeListItemMapper(
    private val context: Context,
    private val onClick: (TroubleCode) -> Unit
) : ListItemMapper<TroubleCode> {
    override fun map(value: TroubleCode): ListItem {
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

    private fun getStatusText(status: TroubleCodeStatus): String {
        return when (status) {
            TroubleCodeStatus.Confirmed -> context.getString(R.string.code_status_confirmed)
            TroubleCodeStatus.Permanent -> context.getString(R.string.code_status_permanent)
            TroubleCodeStatus.Pending -> context.getString(R.string.code_status_pending)
        }
    }

}