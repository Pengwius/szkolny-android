package pl.szczodrzynski.edziennik.ui.modules.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import pl.szczodrzynski.edziennik.App
import pl.szczodrzynski.edziennik.MainActivity
import pl.szczodrzynski.edziennik.R
import pl.szczodrzynski.edziennik.data.db.entity.Message
import pl.szczodrzynski.edziennik.databinding.FragmentMessagesBinding
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyFragment
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyPagerAdapter
import pl.szczodrzynski.edziennik.utils.Themes

class MessagesFragment : Fragment() {
    companion object {
        var pageSelection = 0
    }

    private lateinit var app: App
    private lateinit var activity: MainActivity
    private lateinit var b: FragmentMessagesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = (getActivity() as MainActivity?) ?: return null
        if (context == null)
            return null
        app = activity.application as App
        context!!.theme.applyStyle(Themes.appTheme, true)
        if (app.profile == null)
            return inflater.inflate(R.layout.fragment_loading, container, false)
        // activity, context and profile is valid
        b = FragmentMessagesBinding.inflate(inflater)
        b.refreshLayout.setParent(activity.swipeRefreshLayout)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO check if app, activity, b can be null
        if (app.profile == null || !isAdded)
            return

        val messageId = arguments?.getLong("messageId", -1L) ?: -1L
        if (messageId != -1L) {
            val args = Bundle()
            args.putLong("messageId", messageId)
            arguments!!.remove("messageId")
            activity.loadTarget(MainActivity.TARGET_MESSAGES_DETAILS, args)
            return
        }

        b.viewPager.adapter = Adapter(childFragmentManager).also { adapter ->

            adapter.swipeRefreshLayoutCallback = { isEnabled ->
                b.refreshLayout.isEnabled = isEnabled
            }

            adapter.addFragment(MessagesListFragment().also { fragment ->
                fragment.arguments = Bundle().also { args ->
                    args.putInt("messageType", Message.TYPE_RECEIVED)
                }
            }, getString(R.string.menu_messages_inbox))

            adapter.addFragment(MessagesListFragment().also { fragment ->
                fragment.arguments = Bundle().also { args ->
                    args.putInt("messageType", Message.TYPE_SENT)
                }
            }, getString(R.string.menu_messages_sent))

        }

        b.viewPager.currentItem = pageSelection
        b.viewPager.clearOnPageChangeListeners()
        b.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (b.refreshLayout != null) {
                    b.refreshLayout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
                }
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                pageSelection = position
            }
        })

        b.tabLayout.setupWithViewPager(b.viewPager)

        activity.navView.apply {
            bottomBar.apply {
                fabEnable = true
                fabExtendedText = getString(R.string.compose)
                fabIcon = CommunityMaterial.Icon2.cmd_pencil_outline
            }

            setFabOnClickListener(View.OnClickListener {
                activity.loadTarget(MainActivity.TARGET_MESSAGES_COMPOSE)
            })
        }

        activity.gainAttentionFAB()

        /*if (app.profile.loginStoreType == LOGIN_TYPE_LIBRUS && app.profile.getStudentData("accountPassword", null) == null) {
            MaterialDialog.Builder(activity)
                    .title("Wiadomości w systemie Synergia")
                    .content("Moduł Wiadomości w aplikacji Szkolny.eu jest przeglądarką zasobów szkolnego konta Synergia. Z tego powodu, musisz wpisać swoje hasło do tego konta, aby móc korzystać z tej funkcji.")
                    .positiveText(R.string.ok)
                    .onPositive { dialog, which ->
                        MaterialDialog.Builder(activity)
                                .title("Zaloguj się")
                                .content(Html.fromHtml("Podaj hasło do konta Synergia z loginem <b>" + app.profile.getStudentData("accountLogin", "???") + "</b>"))
                                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                .input(null, null) { dialog1, input ->
                                    app.profile.putStudentData("accountPassword", input.toString())
                                    app.profileSaveFullAsync(app.profile)
                                    EdziennikTask.syncProfile(App.profileId, listOf(
                                            DRAWER_ITEM_MESSAGES to Message.TYPE_RECEIVED,
                                            DRAWER_ITEM_MESSAGES to Message.TYPE_SENT
                                    )).enqueue(context!!)
                                }
                                .positiveText(R.string.ok)
                                .negativeText(R.string.cancel)
                                .show()
                    }
                    .show()
        }*/
    }

    internal class Adapter(manager: FragmentManager) : LazyPagerAdapter(manager) {
        private val mFragmentList = mutableListOf<LazyFragment>()
        private val mFragmentTitleList = mutableListOf<String>()

        override fun getItem(position: Int): LazyFragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: LazyFragment, title: String) {
            fragment.swipeRefreshLayoutCallback = this@Adapter.swipeRefreshLayoutCallback
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}
