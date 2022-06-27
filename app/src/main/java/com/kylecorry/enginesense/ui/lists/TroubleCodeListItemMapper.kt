package com.kylecorry.enginesense.ui.lists

import android.content.Context
import com.kylecorry.andromeda.clipboard.Clipboard
import com.kylecorry.andromeda.core.system.Intents
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.domain.DiagnosticTroubleCodeStatus

class TroubleCodeListItemMapper(
    private val context: Context
) : ListItemMapper<DiagnosticTroubleCode> {
    override fun map(value: DiagnosticTroubleCode): ListItem {
        return ListItem(
            value.code.hashCode().toLong(),
            value.code.uppercase(),
            getStatusText(value.status),
            // TODO: Color based on severity?
            icon = ResourceListIcon(R.drawable.engine, Resources.androidTextColorSecondary(context)),
            longClickAction = {
                Clipboard.copy(context, value.code, context.getString(R.string.copied_to_clipboard))
            }
        ) {
            val intent = Intents.url("https://${value.code}.autotroublecode.com/")
            context.startActivity(intent)
        }
    }

    private fun getStatusText(status: DiagnosticTroubleCodeStatus): String {
        return when (status) {
            DiagnosticTroubleCodeStatus.Confirmed -> context.getString(R.string.code_status_confirmed)
            DiagnosticTroubleCodeStatus.Permanent -> context.getString(R.string.code_status_permanent)
            DiagnosticTroubleCodeStatus.Pending -> context.getString(R.string.code_status_pending)
        }
    }

}