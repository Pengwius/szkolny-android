/*
 * Copyright (c) Kacper Ziubryniewicz 2020-1-6
 */

package pl.szczodrzynski.edziennik.ui.modules.timetable

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyFragment
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyPagerAdapter
import pl.szczodrzynski.edziennik.utils.models.Date
import pl.szczodrzynski.edziennik.utils.models.Week

class TimetablePagerAdapter(
        fragmentManager: FragmentManager,
        private val items: List<Date>,
        private val startHour: Int,
        private val endHour: Int
) : LazyPagerAdapter(fragmentManager) {
    companion object {
        private const val TAG = "TimetablePagerAdapter"
    }

    private val today by lazy { Date.getToday() }
    private val weekStart by lazy { today.weekStart }
    private val weekEnd by lazy { weekStart.clone().stepForward(0, 0, 6) }

    override fun getItem(position: Int): LazyFragment {
        return TimetableDayFragment().apply {
            swipeRefreshLayoutCallback = this@TimetablePagerAdapter.swipeRefreshLayoutCallback
            arguments = Bundle().apply {
                putInt("date", items[position].value)
                putInt("startHour", startHour)
                putInt("endHour", endHour)
            }
        }
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val date = items[position]
        val pageTitle = StringBuilder(Week.getFullDayName(date.weekDay))
        if (date > weekEnd || date < weekStart) {
            pageTitle.append(", ").append(date.stringDm)
        }
        return pageTitle
    }
}
