package com.kylecorry.enginesense.ui.lists

import android.content.Context
import com.kylecorry.andromeda.clipboard.Clipboard
import com.kylecorry.andromeda.core.system.Intents
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.ceres.list.*
import com.kylecorry.enginesense.R
import com.kylecorry.enginesense.domain.DiagnosticTroubleCode
import com.kylecorry.enginesense.domain.DiagnosticTroubleCodeStatus
import com.kylecorry.enginesense.infrastructure.codes.CodeRepo

class TroubleCodeListItemMapper(
    private val context: Context
) : ListItemMapper<DiagnosticTroubleCode> {

    private val repo = CodeRepo.getInstance(context)

    override fun map(value: DiagnosticTroubleCode): ListItem {
        val system = repo.getSystem(value.code)
        val category = repo.getCategory(value.code)
        val name = repo.getName(value.code)
        val isStandard = repo.isStandard(value.code)
        return ListItem(
            value.code.hashCode().toLong(),
            value.code.uppercase(),
            name ?: context.getString(R.string.unknown_code),
            icon = ResourceListIcon(
                R.drawable.engine,
                Resources.androidTextColorSecondary(context)
            ),
            tags = listOf(
                ListItemTag(
                    getStatusText(value.status),
                    null,
                    getStatusColor(value.status)
                )
            ),
            data = listOfNotNull(
                system?.let { ListItemData(it, null) },
                category?.let { ListItemData(it, null) },
                if (isStandard) {
                    ListItemData(context.getString(R.string.code_standard), null)
                } else {
                    ListItemData(context.getString(R.string.code_manufacturer), null)
                }
            ),
            longClickAction = {
                Clipboard.copy(context, value.code, context.getString(R.string.copied_to_clipboard))
            }
        ) {
            val intent = Intents.url("https://obd2pros.com/dtc-codes/${value.code.lowercase()}/")
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

    private fun getStatusColor(status: DiagnosticTroubleCodeStatus): Int {
        return when (status) {
            DiagnosticTroubleCodeStatus.Confirmed -> Resources.color(context, R.color.red)
            DiagnosticTroubleCodeStatus.Permanent -> Resources.color(context, R.color.orange)
            DiagnosticTroubleCodeStatus.Pending -> Resources.color(context, R.color.yellow)
        }
    }

}