/*
 * Copyright (c) Kuba Szczodrzyński 2020-5-12.
 */

package pl.szczodrzynski.edziennik.ui.modules.debug.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import pl.szczodrzynski.edziennik.App
import pl.szczodrzynski.edziennik.databinding.LabItemObjectBinding
import pl.szczodrzynski.edziennik.dp
import pl.szczodrzynski.edziennik.ui.modules.attendance.AttendanceAdapter
import pl.szczodrzynski.edziennik.ui.modules.debug.LabJsonAdapter
import pl.szczodrzynski.edziennik.ui.modules.debug.models.LabJsonObject
import pl.szczodrzynski.edziennik.ui.modules.grades.viewholder.BindableViewHolder
import pl.szczodrzynski.edziennik.utils.Themes

class JsonObjectViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        val b: LabItemObjectBinding = LabItemObjectBinding.inflate(inflater, parent, false)
) : RecyclerView.ViewHolder(b.root), BindableViewHolder<LabJsonObject, LabJsonAdapter> {
    companion object {
        private const val TAG = "JsonObjectViewHolder"
    }

    @SuppressLint("SetTextI18n")
    override fun onBind(activity: AppCompatActivity, app: App, item: LabJsonObject, position: Int, adapter: LabJsonAdapter) {
        val contextWrapper = ContextThemeWrapper(activity, Themes.appTheme)

        b.root.setPadding(item.level * 8.dp + 8.dp, 8.dp, 8.dp, 8.dp)

        b.type.text = "Object"

        b.dropdownIcon.rotation = when (item.state) {
            AttendanceAdapter.STATE_CLOSED -> 0f
            else -> 180f
        }
        b.previewContainer.isInvisible = item.state != AttendanceAdapter.STATE_CLOSED
        b.summaryContainer.isInvisible = item.state == AttendanceAdapter.STATE_CLOSED

        b.key.text = item.key.substringAfterLast(":")
        b.previewContainer.text = item.jsonObject.toString().take(200)
        b.summaryContainer.text = item.jsonObject.size().toString() + " elements"
    }
}
