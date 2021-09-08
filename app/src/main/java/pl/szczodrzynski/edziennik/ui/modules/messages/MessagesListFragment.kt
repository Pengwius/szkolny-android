/*
 * Copyright (c) Kuba Szczodrzyński 2020-4-4.
 */

package pl.szczodrzynski.edziennik.ui.modules.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import pl.szczodrzynski.edziennik.*
import pl.szczodrzynski.edziennik.data.db.entity.Message
import pl.szczodrzynski.edziennik.data.db.entity.Teacher
import pl.szczodrzynski.edziennik.databinding.MessagesListFragmentBinding
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyFragment
import pl.szczodrzynski.edziennik.ui.modules.messages.models.MessagesSearch
import pl.szczodrzynski.edziennik.utils.SimpleDividerItemDecoration
import kotlin.coroutines.CoroutineContext

class MessagesListFragment : LazyFragment(), CoroutineScope {
    companion object {
        private const val TAG = "MessagesListFragment"
    }

    private lateinit var app: App
    private lateinit var activity: MainActivity
    private lateinit var b: MessagesListFragmentBinding
    private var adapter: MessagesAdapter? = null

    private val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    // local/private variables go here
    var teachers = listOf<Teacher>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = (getActivity() as MainActivity?) ?: return null
        context ?: return null
        app = activity.application as App
        b = MessagesListFragmentBinding.inflate(inflater)
        return b.root
    }

    override fun onPageCreated(): Boolean { startCoroutineTimer(100L) {
        val messageType = arguments.getInt("messageType", Message.TYPE_RECEIVED)
        var topPosition = arguments.getInt("topPosition", NO_POSITION)
        var bottomPosition = arguments.getInt("bottomPosition", NO_POSITION)
        val searchText = arguments.getString("searchText", "")

        teachers = withContext(Dispatchers.Default) {
            app.db.teacherDao().getAllNow(App.profileId)
        }

        adapter = MessagesAdapter(activity, teachers) {
            activity.loadTarget(MainActivity.TARGET_MESSAGES_DETAILS, Bundle(
                    "messageId" to it.id
            ))
        }

        app.db.messageDao().getAllByType(App.profileId, messageType).observe(this@MessagesListFragment, Observer { messages ->
            if (!isAdded) return@Observer

            messages.forEach { message ->
                message.recipients?.removeAll { it.profileId != message.profileId }
                message.recipients?.forEach { recipient ->
                    if (recipient.fullName == null) {
                        recipient.fullName = teachers.firstOrNull { it.id == recipient.id }?.fullName ?: ""
                    }
                }
            }

            // load & configure the adapter
            val items = messages.toMutableList<Any>()
            items.add(0, MessagesSearch().also {
                it.searchText = searchText
            })

            adapter?.items = items
            adapter?.allItems = items

            if (items.isNotNullNorEmpty() && b.list.adapter == null) {
                if (searchText.isNotBlank())
                    adapter?.filter?.filter(searchText) {
                        b.list.adapter = adapter
                    }
                else
                    b.list.adapter = adapter

                b.list.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(SimpleDividerItemDecoration(context))
                    if (messageType in Message.TYPE_RECEIVED..Message.TYPE_SENT)
                        addOnScrollListener(onScrollListener)
                }
            }

            setSwipeToRefresh(messageType in Message.TYPE_RECEIVED..Message.TYPE_SENT && items.isNullOrEmpty())

            (b.list.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
                if (topPosition != NO_POSITION && topPosition > layoutManager.findLastCompletelyVisibleItemPosition()) {
                    b.list.scrollToPosition(topPosition)
                } else if (bottomPosition != NO_POSITION && bottomPosition < layoutManager.findFirstVisibleItemPosition()) {
                    b.list.scrollToPosition(bottomPosition)
                }
                topPosition = NO_POSITION
                bottomPosition = NO_POSITION
            }

            // show/hide relevant views
            b.progressBar.isVisible = false
            if (items.isNullOrEmpty()) {
                b.list.isVisible = false
                b.noData.isVisible = true
            } else {
                b.list.isVisible = true
                b.noData.isVisible = false
            }
        })
    }; return true }

    override fun onDestroy() {
        super.onDestroy()
        if (!isAdded)
            return
        val layoutManager = (b.list.layoutManager as? LinearLayoutManager)
        val searchItem = adapter?.items?.firstOrNull { it is MessagesSearch } as? MessagesSearch

        onPageDestroy?.invoke(position, Bundle(
            "topPosition" to layoutManager?.findFirstVisibleItemPosition(),
            "bottomPosition" to layoutManager?.findLastCompletelyVisibleItemPosition(),
            "searchText" to searchItem?.searchText?.toString()
        ))
    }
}
