/*
 * Copyright (c) Kuba Szczodrzyński 2020-5-12.
 */

package pl.szczodrzynski.edziennik.ui.modules.debug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.szczodrzynski.edziennik.*
import pl.szczodrzynski.edziennik.data.db.entity.Event
import pl.szczodrzynski.edziennik.databinding.LabFragmentBinding
import pl.szczodrzynski.edziennik.ui.dialogs.profile.ProfileRemoveDialog
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyFragment
import pl.szczodrzynski.edziennik.utils.TextInputDropDown
import pl.szczodrzynski.fslogin.decode
import kotlin.coroutines.CoroutineContext

class LabPageFragment : LazyFragment(), CoroutineScope {
    companion object {
        private const val TAG = "LabPageFragment"
    }

    private lateinit var app: App
    private lateinit var activity: MainActivity
    private lateinit var b: LabFragmentBinding

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    // local/private variables go here

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = (getActivity() as MainActivity?) ?: return null
        context ?: return null
        app = activity.application as App
        b = LabFragmentBinding.inflate(inflater)
        return b.root
    }

    override fun onPageCreated(): Boolean {
        b.app = app

        b.last10unseen.onClick {
            launch(Dispatchers.Default) {
                val events = app.db.eventDao().getAllNow(App.profileId)
                val ids = events.sortedBy { it.date }.filter { it.type == Event.TYPE_HOMEWORK }.takeLast(10)
                ids.forEach {
                    app.db.metadataDao().setSeen(App.profileId, it, false)
                }
            }
        }

        b.rodo.onClick {
            app.db.teacherDao().query(SimpleSQLiteQuery("UPDATE teachers SET teacherSurname = \"\" WHERE profileId = ${App.profileId}"))
        }
        
        b.fullSync.onClick { 
            app.db.query(SimpleSQLiteQuery("UPDATE profiles SET empty = 1 WHERE profileId = ${App.profileId}"))
            app.db.query(SimpleSQLiteQuery("DELETE FROM endpointTimers WHERE profileId = ${App.profileId}"))
        }

        b.clearProfile.onClick {
            ProfileRemoveDialog(activity, App.profileId, "FAKE", noProfileRemoval = true)
        }

        b.removeHomework.onClick {
            app.db.eventDao().getRawNow("UPDATE events SET homeworkBody = NULL WHERE profileId = ${App.profileId}")
        }

        b.unarchive.onClick {
            app.profile.archived = false
            app.profile.archiveId = null
            app.profileSave()
        }

        val profiles = app.db.profileDao().allNow
        b.profile.clear()
        b.profile += profiles.map { TextInputDropDown.Item(it.id.toLong(), "${it.id} ${it.name} archived ${it.archived}", tag = it) }
        b.profile.select(app.profileId.toLong())
        b.profile.setOnChangeListener {
            activity.loadProfile(it.id.toInt())
            return@setOnChangeListener true
        }

        val colorSecondary = android.R.attr.textColorSecondary.resolveAttr(activity)
        startCoroutineTimer(500L, 300L) {
            val text = app.cookieJar.sessionCookies
                    .map { it.cookie }
                    .sortedBy { it.domain() }
                    .groupBy { it.domain() }
                    .map {
                        listOf(
                                it.key.asBoldSpannable(),
                                ":\n",
                                it.value
                                        .sortedBy { it.name() }
                                        .map {
                                            listOf(
                                                    "    ",
                                                    it.name(),
                                                    "=",
                                                    it.value().decode().take(40).asItalicSpannable().asColoredSpannable(colorSecondary)
                                            ).concat("")
                                        }.concat("\n")
                        ).concat("")
                    }.concat("\n\n")
            b.cookies.text = text
        }

        return true
    }
}
