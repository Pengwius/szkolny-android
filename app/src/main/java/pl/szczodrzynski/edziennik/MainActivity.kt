package pl.szczodrzynski.edziennik

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import com.danimahardhika.cafebar.CafeBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import com.mikepenz.iconics.typeface.library.szkolny.font.SzkolnyFont
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.*
import com.mikepenz.materialdrawer.model.utils.withIsHiddenInMiniDrawer
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pl.droidsonroids.gif.GifDrawable
import pl.szczodrzynski.edziennik.data.api.edziennik.EdziennikTask
import pl.szczodrzynski.edziennik.data.api.events.*
import pl.szczodrzynski.edziennik.data.api.models.ApiError
import pl.szczodrzynski.edziennik.data.db.entity.LoginStore
import pl.szczodrzynski.edziennik.data.db.entity.Metadata.*
import pl.szczodrzynski.edziennik.data.db.entity.Profile
import pl.szczodrzynski.edziennik.databinding.ActivitySzkolnyBinding
import pl.szczodrzynski.edziennik.sync.AppManagerDetectedEvent
import pl.szczodrzynski.edziennik.sync.SyncWorker
import pl.szczodrzynski.edziennik.sync.UpdateWorker
import pl.szczodrzynski.edziennik.ui.dialogs.ServerMessageDialog
import pl.szczodrzynski.edziennik.ui.dialogs.changelog.ChangelogDialog
import pl.szczodrzynski.edziennik.ui.dialogs.event.EventManualDialog
import pl.szczodrzynski.edziennik.ui.dialogs.settings.ProfileRemoveDialog
import pl.szczodrzynski.edziennik.ui.dialogs.sync.SyncViewListDialog
import pl.szczodrzynski.edziennik.ui.modules.agenda.AgendaFragment
import pl.szczodrzynski.edziennik.ui.modules.announcements.AnnouncementsFragment
import pl.szczodrzynski.edziennik.ui.modules.attendance.AttendanceFragment
import pl.szczodrzynski.edziennik.ui.modules.base.MainSnackbar
import pl.szczodrzynski.edziennik.ui.modules.behaviour.BehaviourFragment
import pl.szczodrzynski.edziennik.ui.modules.debug.DebugFragment
import pl.szczodrzynski.edziennik.ui.modules.debug.LabFragment
import pl.szczodrzynski.edziennik.ui.modules.error.ErrorSnackbar
import pl.szczodrzynski.edziennik.ui.modules.feedback.FeedbackFragment
import pl.szczodrzynski.edziennik.ui.modules.feedback.HelpFragment
import pl.szczodrzynski.edziennik.ui.modules.grades.GradesListFragment
import pl.szczodrzynski.edziennik.ui.modules.grades.editor.GradesEditorFragment
import pl.szczodrzynski.edziennik.ui.modules.home.HomeFragment
import pl.szczodrzynski.edziennik.ui.modules.homework.HomeworkFragment
import pl.szczodrzynski.edziennik.ui.modules.login.LoginActivity
import pl.szczodrzynski.edziennik.ui.modules.messages.MessageFragment
import pl.szczodrzynski.edziennik.ui.modules.messages.MessagesFragment
import pl.szczodrzynski.edziennik.ui.modules.messages.compose.MessagesComposeFragment
import pl.szczodrzynski.edziennik.ui.modules.notifications.NotificationsListFragment
import pl.szczodrzynski.edziennik.ui.modules.settings.ProfileManagerFragment
import pl.szczodrzynski.edziennik.ui.modules.settings.SettingsNewFragment
import pl.szczodrzynski.edziennik.ui.modules.timetable.TimetableFragment
import pl.szczodrzynski.edziennik.ui.modules.webpush.WebPushFragment
import pl.szczodrzynski.edziennik.utils.SwipeRefreshLayoutNoTouch
import pl.szczodrzynski.edziennik.utils.Themes
import pl.szczodrzynski.edziennik.utils.Utils
import pl.szczodrzynski.edziennik.utils.Utils.d
import pl.szczodrzynski.edziennik.utils.Utils.dpToPx
import pl.szczodrzynski.edziennik.utils.appManagerIntentList
import pl.szczodrzynski.edziennik.utils.models.Date
import pl.szczodrzynski.edziennik.utils.models.NavTarget
import pl.szczodrzynski.navlib.*
import pl.szczodrzynski.navlib.SystemBarsUtil.Companion.COLOR_HALF_TRANSPARENT
import pl.szczodrzynski.navlib.bottomsheet.NavBottomSheet
import pl.szczodrzynski.navlib.bottomsheet.items.BottomSheetPrimaryItem
import pl.szczodrzynski.navlib.bottomsheet.items.BottomSheetSeparatorItem
import pl.szczodrzynski.navlib.drawer.NavDrawer
import pl.szczodrzynski.navlib.drawer.items.DrawerPrimaryItem
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object {

        var useOldMessages = false

        const val TAG = "MainActivity"

        const val REQUEST_LOGIN_ACTIVITY = 20222

        const val DRAWER_PROFILE_ADD_NEW = 200
        const val DRAWER_PROFILE_SYNC_ALL = 201
        const val DRAWER_PROFILE_EXPORT_DATA = 202
        const val DRAWER_PROFILE_MANAGE = 203
        const val DRAWER_PROFILE_MARK_ALL_AS_READ = 204
        const val DRAWER_ITEM_HOME = 1
        const val DRAWER_ITEM_TIMETABLE = 11
        const val DRAWER_ITEM_AGENDA = 12
        const val DRAWER_ITEM_GRADES = 13
        const val DRAWER_ITEM_MESSAGES = 17
        const val DRAWER_ITEM_HOMEWORK = 14
        const val DRAWER_ITEM_BEHAVIOUR = 15
        const val DRAWER_ITEM_ATTENDANCE = 16
        const val DRAWER_ITEM_ANNOUNCEMENTS = 18
        const val DRAWER_ITEM_NOTIFICATIONS = 20
        const val DRAWER_ITEM_SETTINGS = 101
        const val DRAWER_ITEM_DEBUG = 102

        const val TARGET_GRADES_EDITOR = 501
        const val TARGET_HELP = 502
        const val TARGET_FEEDBACK = 120
        const val TARGET_MESSAGES_DETAILS = 503
        const val TARGET_MESSAGES_COMPOSE = 504
        const val TARGET_WEB_PUSH = 140
        const val TARGET_LAB = 1000

        const val HOME_ID = DRAWER_ITEM_HOME

        val navTargetList: List<NavTarget> by lazy {
            val list: MutableList<NavTarget> = mutableListOf()

            // home item
            list += NavTarget(DRAWER_ITEM_HOME, R.string.menu_home_page, HomeFragment::class)
                    .withTitle(R.string.app_name)
                    .withIcon(CommunityMaterial.Icon2.cmd_home_outline)
                    .isInDrawer(true)
                    .isStatic(true)
                    .withPopToHome(false)

            list += NavTarget(DRAWER_ITEM_TIMETABLE, R.string.menu_timetable, TimetableFragment::class)
                    .withIcon(CommunityMaterial.Icon2.cmd_timetable)
                    .withBadgeTypeId(TYPE_LESSON_CHANGE)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_AGENDA, R.string.menu_agenda, AgendaFragment::class)
                    .withIcon(CommunityMaterial.Icon.cmd_calendar_outline)
                    .withBadgeTypeId(TYPE_EVENT)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_GRADES, R.string.menu_grades, GradesListFragment::class)
                    .withIcon(CommunityMaterial.Icon2.cmd_numeric_5_box_outline)
                    .withBadgeTypeId(TYPE_GRADE)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_MESSAGES, R.string.menu_messages, MessagesFragment::class)
                    .withIcon(CommunityMaterial.Icon.cmd_email_outline)
                    .withBadgeTypeId(TYPE_MESSAGE)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_HOMEWORK, R.string.menu_homework, HomeworkFragment::class)
                    .withIcon(SzkolnyFont.Icon.szf_notebook_outline)
                    .withBadgeTypeId(TYPE_HOMEWORK)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_BEHAVIOUR, R.string.menu_notices, BehaviourFragment::class)
                    .withIcon(CommunityMaterial.Icon.cmd_emoticon_outline)
                    .withBadgeTypeId(TYPE_NOTICE)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_ATTENDANCE, R.string.menu_attendance, AttendanceFragment::class)
                    .withIcon(CommunityMaterial.Icon.cmd_calendar_remove_outline)
                    .withBadgeTypeId(TYPE_ATTENDANCE)
                    .isInDrawer(true)

            list += NavTarget(DRAWER_ITEM_ANNOUNCEMENTS, R.string.menu_announcements, AnnouncementsFragment::class)
                    .withIcon(CommunityMaterial.Icon.cmd_bullhorn_outline)
                    .withBadgeTypeId(TYPE_ANNOUNCEMENT)
                    .isInDrawer(true)


            // static drawer items
            list += NavTarget(DRAWER_ITEM_NOTIFICATIONS, R.string.menu_notifications, NotificationsListFragment::class)
                    .withIcon(CommunityMaterial.Icon.cmd_bell_ring_outline)
                    .isInDrawer(true)
                    .isStatic(true)
                    .isBelowSeparator(true)

            list += NavTarget(DRAWER_ITEM_SETTINGS, R.string.menu_settings, SettingsNewFragment::class)
                    .withIcon(CommunityMaterial.Icon2.cmd_settings_outline)
                    .isInDrawer(true)
                    .isStatic(true)
                    .isBelowSeparator(true)


            // profile settings items
            list += NavTarget(DRAWER_PROFILE_ADD_NEW, R.string.menu_add_new_profile, null)
                    .withIcon(CommunityMaterial.Icon2.cmd_plus)
                    .withDescription(R.string.drawer_add_new_profile_desc)
                    .isInProfileList(true)

            list += NavTarget(DRAWER_PROFILE_MANAGE, R.string.menu_manage_profiles, ProfileManagerFragment::class)
                    .withTitle(R.string.title_profile_manager)
                    .withIcon(CommunityMaterial.Icon.cmd_account_group)
                    .withDescription(R.string.drawer_manage_profiles_desc)
                    .isInProfileList(false)

            list += NavTarget(DRAWER_PROFILE_MARK_ALL_AS_READ, R.string.menu_mark_everything_as_read, null)
                    .withIcon(CommunityMaterial.Icon.cmd_eye_check_outline)
                    .isInProfileList(true)

            list += NavTarget(DRAWER_PROFILE_SYNC_ALL, R.string.menu_sync_all, null)
                    .withIcon(CommunityMaterial.Icon.cmd_download_outline)
                    .isInProfileList(true)


            // other target items, not directly navigated
            list += NavTarget(TARGET_GRADES_EDITOR, R.string.menu_grades_editor, GradesEditorFragment::class)
            list += NavTarget(TARGET_HELP, R.string.menu_help, HelpFragment::class)
            list += NavTarget(TARGET_FEEDBACK, R.string.menu_feedback, FeedbackFragment::class)
            list += NavTarget(TARGET_MESSAGES_DETAILS, R.string.menu_message, MessageFragment::class).withPopTo(DRAWER_ITEM_MESSAGES)
            list += NavTarget(TARGET_MESSAGES_COMPOSE, R.string.menu_message_compose, MessagesComposeFragment::class)
            list += NavTarget(TARGET_WEB_PUSH, R.string.menu_web_push, WebPushFragment::class)
            if (App.debugMode) {
                list += NavTarget(DRAWER_ITEM_DEBUG, R.string.menu_debug, DebugFragment::class)
                list += NavTarget(TARGET_LAB, R.string.menu_lab, LabFragment::class)
                        .withIcon(CommunityMaterial.Icon.cmd_flask_outline)
                        .isInDrawer(true)
                        .isBelowSeparator(true)
                        .isStatic(true)
            }

            list
        }
    }

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    val b: ActivitySzkolnyBinding by lazy { ActivitySzkolnyBinding.inflate(layoutInflater) }
    val navView: NavView by lazy { b.navView }
    val drawer: NavDrawer by lazy { navView.drawer }
    val bottomSheet: NavBottomSheet by lazy { navView.bottomSheet }
    val mainSnackbar: MainSnackbar by lazy { MainSnackbar(this) }
    val errorSnackbar: ErrorSnackbar by lazy { ErrorSnackbar(this) }

    val swipeRefreshLayout: SwipeRefreshLayoutNoTouch by lazy { b.swipeRefreshLayout }

    val app: App by lazy {
        applicationContext as App
    }

    private val fragmentManager by lazy { supportFragmentManager }
    private lateinit var navTarget: NavTarget
    private var navArguments: Bundle? = null
    val navTargetId
        get() = navTarget.id

    private val navBackStack = mutableListOf<Pair<NavTarget, Bundle?>>()
    private var navLoading = true

    /*     ____           _____                _
          / __ \         / ____|              | |
         | |  | |_ __   | |     _ __ ___  __ _| |_ ___
         | |  | | '_ \  | |    | '__/ _ \/ _` | __/ _ \
         | |__| | | | | | |____| | |  __/ (_| | ||  __/
          \____/|_| |_|  \_____|_|  \___|\__,_|\__\__*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        d(TAG, "Activity created")

        setTheme(Themes.appTheme)

        app.config.ui.language?.let {
            setLanguage(it)
        }

        if (App.profileId == 0) {
            onProfileListEmptyEvent(ProfileListEmptyEvent())
            return
        }

        d(TAG, "Profile is valid, inflating views")

        setContentView(b.root)

        mainSnackbar.setCoordinator(b.navView.coordinator, b.navView.bottomBar)
        errorSnackbar.setCoordinator(b.navView.coordinator, b.navView.bottomBar)

        when {
            BuildConfig.VERSION_NAME.contains("nightly") -> {
                b.nightlyText.isVisible = true
                b.nightlyText.text = "Nightly\n"+BuildConfig.VERSION_NAME.substringAfterLast(".")
            }
            BuildConfig.VERSION_NAME.contains("daily") -> {
                b.nightlyText.isVisible = true
                b.nightlyText.text = "Daily\n"+BuildConfig.VERSION_NAME.substringAfterLast(".")
            }
            BuildConfig.DEBUG -> {
                b.nightlyText.isVisible = true
                b.nightlyText.text = "Debug\n"+BuildConfig.VERSION_NAME
            }
            else -> b.nightlyText.isVisible = false
        }

        navLoading = true

        b.navView.apply {
            drawer.init(this@MainActivity)

            SystemBarsUtil(this@MainActivity).run {
                //paddingByKeyboard = b.navView
                appFullscreen = false
                statusBarColor = getColorFromAttr(context, android.R.attr.colorBackground)
                statusBarDarker = false
                statusBarFallbackLight = COLOR_HALF_TRANSPARENT
                statusBarFallbackGradient = COLOR_HALF_TRANSPARENT
                navigationBarTransparent = false

                b.navView.configSystemBarsUtil(this)

                // fix for setting status bar color to window color, outside of navlib
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = statusBarColor
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ColorUtils.calculateLuminance(statusBarColor) > 0.6) {
                    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }

                // TODO fix navlib navbar detection, orientation change issues, status bar color setting if not fullscreen

                commit()
            }

            toolbar.apply {
                subtitleFormat = R.string.toolbar_subtitle
                subtitleFormatWithUnread = R.plurals.toolbar_subtitle_with_unread
            }

            bottomBar.apply {
                fabEnable = false
                fabExtendable = true
                fabExtended = false
                fabGravity = Gravity.CENTER
                if (Themes.isDark) {
                    setBackgroundColor(blendColors(
                            getColorFromAttr(context, R.attr.colorSurface),
                            getColorFromRes(R.color.colorSurface_4dp)
                    ))
                    elevation = dpToPx(4).toFloat()
                }
            }

            bottomSheet.apply {
                removeAllItems()
                toggleGroupEnabled = false
                textInputEnabled = false
                onCloseListener = {
                    if (!app.config.ui.bottomSheetOpened)
                        app.config.ui.bottomSheetOpened = true
                }
            }

            drawer.apply {
                setAccountHeaderBackground(app.config.ui.headerBackground)

                drawerProfileListEmptyListener = {
                    onProfileListEmptyEvent(ProfileListEmptyEvent())
                }
                drawerItemSelectedListener = { id, position, drawerItem ->
                    loadTarget(id)
                    true
                }
                drawerProfileSelectedListener = { id, profile, _, _ ->
                    loadProfile(id)
                    false
                }
                drawerProfileLongClickListener = { _, profile, _, view ->
                    if (view != null && profile is ProfileDrawerItem) {
                        showProfileContextMenu(profile, view)
                        true
                    }
                    else {
                        false
                    }
                }
                drawerProfileImageLongClickListener = drawerProfileLongClickListener
                drawerProfileSettingClickListener = this@MainActivity.profileSettingClickListener

                miniDrawerVisibleLandscape = null
                miniDrawerVisiblePortrait = app.config.ui.miniMenuVisible
            }
        }

        navTarget = navTargetList[0]

        if (savedInstanceState != null) {
            intent?.putExtras(savedInstanceState)
            savedInstanceState.clear()
        }

        app.db.profileDao().all.observe(this, Observer { profiles ->
            drawer.setProfileList(profiles.filter { it.id >= 0 && !it.archived }.toMutableList())
            //prepend the archived profile if loaded
            if (app.profile.archived) {
                drawer.prependProfile(Profile(
                        id = app.profile.id,
                        loginStoreId = app.profile.loginStoreId,
                        loginStoreType = app.profile.loginStoreType,
                        name = app.profile.name,
                        subname = "Archiwum - ${app.profile.subname}"
                ).also {
                    it.archived = true
                })
            }
            drawer.currentProfile = App.profileId
        })

        setDrawerItems()

        handleIntent(intent?.extras)

        app.db.metadataDao().unreadCounts.observe(this, Observer { unreadCounters ->
            unreadCounters.map {
                it.type = it.thingType
            }
            drawer.setUnreadCounterList(unreadCounters)
        })

        b.swipeRefreshLayout.isEnabled = true
        b.swipeRefreshLayout.setOnRefreshListener { this.syncCurrentFeature() }
        b.swipeRefreshLayout.setColorSchemeResources(
                R.color.md_blue_500,
                R.color.md_amber_500,
                R.color.md_green_500
        )

        SyncWorker.scheduleNext(app)
        UpdateWorker.scheduleNext(app)

        // if loaded profile is archived, switch to the up-to-date version of it
        if (app.profile.archived) {
            launch {
                if (app.profile.archiveId != null) {
                    val profile = withContext(Dispatchers.IO) {
                        app.db.profileDao().getNotArchivedOf(app.profile.archiveId!!)
                    }
                    if (profile != null)
                        loadProfile(profile)
                    else
                        loadProfile(0)
                } else {
                    loadProfile(0)
                }
            }
        }

        // APP BACKGROUND
        if (app.config.ui.appBackground != null) {
            try {
                app.config.ui.appBackground?.let {
                    var bg = it
                    val bgDir = File(Environment.getExternalStoragePublicDirectory("Szkolny.eu"), "bg")
                    if (bgDir.exists()) {
                        val files = bgDir.listFiles()
                        val r = Random()
                        val i = r.nextInt(files.size)
                        bg = files[i].toString()
                    }
                    val linearLayout = b.root
                    if (bg.endsWith(".gif")) {
                        linearLayout.background = GifDrawable(bg)
                    } else {
                        linearLayout.background = BitmapDrawable.createFromPath(bg)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // IT'S WINTER MY DUDES
        val today = Date.getToday()
        if ((today.month == 12 || today.month == 1) && app.config.ui.snowfall) {
            b.rootFrame.addView(layoutInflater.inflate(R.layout.snowfall, b.rootFrame, false))
        }

        // WHAT'S NEW DIALOG
        if (app.config.appVersion < BuildConfig.VERSION_CODE) {
            // force an AppSync after update
            app.config.sync.lastAppSync = 0L
            ChangelogDialog(this)
            if (app.config.appVersion < 170) {
                //Intent intent = new Intent(this, ChangelogIntroActivity.class);
                //startActivity(intent);
            } else {
                app.config.appVersion = BuildConfig.VERSION_CODE
            }
        }

        // RATE SNACKBAR
        if (app.config.appRateSnackbarTime != 0L && app.config.appRateSnackbarTime <= System.currentTimeMillis()) {
            navView.coordinator.postDelayed({
                CafeBar.builder(this)
                        .content(R.string.rate_snackbar_text)
                        .icon(IconicsDrawable(this).icon(CommunityMaterial.Icon2.cmd_star_outline).size(IconicsSize.dp(20)).color(IconicsColor.colorInt(Themes.getPrimaryTextColor(this))))
                        .positiveText(R.string.rate_snackbar_positive)
                        .positiveColor(-0xb350b0)
                        .negativeText(R.string.rate_snackbar_negative)
                        .negativeColor(0xff666666.toInt())
                        .neutralText(R.string.rate_snackbar_neutral)
                        .neutralColor(0xff666666.toInt())
                        .onPositive { cafeBar ->
                            Utils.openGooglePlay(this)
                            cafeBar.dismiss()
                            app.config.appRateSnackbarTime = 0
                        }
                        .onNegative { cafeBar ->
                            Toast.makeText(this, "Szkoda, opinie innych pomagają mi rozwijać aplikację.", Toast.LENGTH_LONG).show()
                            cafeBar.dismiss()
                            app.config.appRateSnackbarTime = 0
                        }
                        .onNeutral { cafeBar ->
                            Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
                            cafeBar.dismiss()
                            app.config.appRateSnackbarTime = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000
                        }
                        .autoDismiss(false)
                        .swipeToDismiss(true)
                        .floating(true)
                        .show()
            }, 10000)
        }

        // CONTEXT MENU ITEMS
        bottomSheet.removeAllItems()
        bottomSheet.appendItems(
                BottomSheetPrimaryItem(false)
                        .withTitle(R.string.menu_sync)
                        .withIcon(CommunityMaterial.Icon.cmd_download_outline)
                        .withOnClickListener(View.OnClickListener {
                            bottomSheet.close()
                            SyncViewListDialog(this, navTargetId)
                        }),
                BottomSheetSeparatorItem(false),
                BottomSheetPrimaryItem(false)
                        .withTitle(R.string.menu_settings)
                        .withIcon(CommunityMaterial.Icon2.cmd_settings_outline)
                        .withOnClickListener(View.OnClickListener { loadTarget(DRAWER_ITEM_SETTINGS) }),
                BottomSheetPrimaryItem(false)
                        .withTitle(R.string.menu_feedback)
                        .withIcon(CommunityMaterial.Icon2.cmd_help_circle_outline)
                        .withOnClickListener(View.OnClickListener { loadTarget(TARGET_FEEDBACK) })
        )
        if (App.debugMode) {
            bottomSheet += BottomSheetPrimaryItem(false)
                    .withTitle(R.string.menu_debug)
                    .withIcon(CommunityMaterial.Icon.cmd_android_studio)
                    .withOnClickListener(View.OnClickListener { loadTarget(DRAWER_ITEM_DEBUG) })
        }
    }

    private var profileSettingClickListener = { id: Int, view: View? ->
        when (id) {
            DRAWER_PROFILE_ADD_NEW -> {
                startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_LOGIN_ACTIVITY)
            }
            DRAWER_PROFILE_SYNC_ALL -> {
                EdziennikTask.sync().enqueue(this)
            }
            DRAWER_PROFILE_MARK_ALL_AS_READ -> { launch {
                withContext(Dispatchers.Default) {
                    app.db.profileDao().allNow.forEach { profile ->
                        if (profile.loginStoreType != LoginStore.LOGIN_TYPE_LIBRUS)
                            app.db.metadataDao().setAllSeenExceptMessagesAndAnnouncements(profile.id, true)
                        else
                            app.db.metadataDao().setAllSeenExceptMessages(profile.id, true)
                    }
                }
                Toast.makeText(this@MainActivity, R.string.main_menu_mark_as_read_success, Toast.LENGTH_SHORT).show()
            }}
            else -> {
                loadTarget(id)
            }
        }
        false
    }

    /*     _____
          / ____|
         | (___  _   _ _ __   ___
          \___ \| | | | '_ \ / __|
          ____) | |_| | | | | (__
         |_____/ \__, |_| |_|\___|
                  __/ |
                 |__*/
    fun syncCurrentFeature() {
        if (app.profile.archived) {
            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.profile_archived_title)
                    .setMessage(
                            R.string.profile_archived_text,
                            app.profile.studentSchoolYearStart,
                            app.profile.studentSchoolYearStart + 1
                    )
                    .setPositiveButton(R.string.ok, null)
                    .show()
            swipeRefreshLayout.isRefreshing = false
            return
        }
        if (app.profile.shouldArchive()) {
            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.profile_archiving_title)
                    .setMessage(
                            R.string.profile_archiving_format,
                            app.profile.dateYearEnd.formattedString
                    )
                    .setPositiveButton(R.string.ok, null)
                    .show()
        }
        if (app.profile.isBeforeYear()) {
            MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.profile_year_not_started_title)
                    .setMessage(
                            R.string.profile_year_not_started_format,
                            app.profile.dateSemester1Start.formattedString
                    )
                    .setPositiveButton(R.string.ok, null)
                    .show()
            swipeRefreshLayout.isRefreshing = false
            return
        }
        swipeRefreshLayout.isRefreshing = true
        Toast.makeText(this, fragmentToSyncName(navTargetId), Toast.LENGTH_SHORT).show()
        val fragmentParam = when (navTargetId) {
            DRAWER_ITEM_MESSAGES -> MessagesFragment.pageSelection
            else -> 0
        }
        val arguments = when (navTargetId) {
            DRAWER_ITEM_TIMETABLE -> JsonObject("weekStart" to TimetableFragment.pageSelection?.weekStart?.stringY_m_d)
            else -> null
        }
        EdziennikTask.syncProfile(
                App.profileId,
                listOf(navTargetId to fragmentParam),
                arguments = arguments
        ).enqueue(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onApiTaskStartedEvent(event: ApiTaskStartedEvent) {
        swipeRefreshLayout.isRefreshing = true
        if (event.profileId == App.profileId) {
            navView.toolbar.apply {
                subtitleFormat = null
                subtitleFormatWithUnread = null
                subtitle = getString(R.string.toolbar_subtitle_syncing)
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onProfileListEmptyEvent(event: ProfileListEmptyEvent) {
        d(TAG, "Profile list is empty. Launch LoginActivity.")
        app.config.loginFinished = false
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onApiTaskProgressEvent(event: ApiTaskProgressEvent) {
        if (event.profileId == App.profileId) {
            navView.toolbar.apply {
                subtitleFormat = null
                subtitleFormatWithUnread = null
                subtitle = if (event.progress < 0f)
                    event.progressText ?: ""
                else
                    getString(R.string.toolbar_subtitle_syncing_format, event.progress.roundToInt(), event.progressText ?: "")

            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onApiTaskFinishedEvent(event: ApiTaskFinishedEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        if (event.profileId == App.profileId) {
            navView.toolbar.apply {
                subtitleFormat = R.string.toolbar_subtitle
                subtitleFormatWithUnread = R.plurals.toolbar_subtitle_with_unread
                subtitle = "Gotowe"
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onApiTaskAllFinishedEvent(event: ApiTaskAllFinishedEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        swipeRefreshLayout.isRefreshing = false
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onApiTaskErrorEvent(event: ApiTaskErrorEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        navView.toolbar.apply {
            subtitleFormat = R.string.toolbar_subtitle
            subtitleFormatWithUnread = R.plurals.toolbar_subtitle_with_unread
            subtitle = "Gotowe"
        }
        mainSnackbar.dismiss()
        errorSnackbar.addError(event.error).show()
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAppManagerDetectedEvent(event: AppManagerDetectedEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        if (app.config.sync.dontShowAppManagerDialog)
            return
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_manager_dialog_title)
                .setMessage(R.string.app_manager_dialog_text)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    try {
                        for (intent in appManagerIntentList) {
                            if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                                startActivity(intent)
                            }
                        }
                    } catch (e: Exception) {
                        try {
                            startActivity(Intent(Settings.ACTION_SETTINGS))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this, R.string.app_manager_open_failed, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNeutralButton(R.string.dont_ask_again) { dialog, which ->
                    app.config.sync.dontShowAppManagerDialog = true
                }
                .setCancelable(false)
                .show()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserActionRequiredEvent(event: UserActionRequiredEvent) {
        app.userActionManager.execute(this, event.profileId, event.type)
    }

    private fun fragmentToSyncName(currentFragment: Int): Int {
        return when (currentFragment) {
            DRAWER_ITEM_TIMETABLE -> R.string.sync_feature_timetable
            DRAWER_ITEM_AGENDA -> R.string.sync_feature_agenda
            DRAWER_ITEM_GRADES -> R.string.sync_feature_grades
            DRAWER_ITEM_HOMEWORK -> R.string.sync_feature_homework
            DRAWER_ITEM_BEHAVIOUR -> R.string.sync_feature_notices
            DRAWER_ITEM_ATTENDANCE -> R.string.sync_feature_attendance
            DRAWER_ITEM_MESSAGES -> when (MessagesFragment.pageSelection) {
                1 -> R.string.sync_feature_messages_outbox
                else -> R.string.sync_feature_messages_inbox
            }
            DRAWER_ITEM_ANNOUNCEMENTS -> R.string.sync_feature_announcements
            else -> R.string.sync_feature_syncing_all
        }
    }

    /*    _____       _             _
         |_   _|     | |           | |
           | |  _ __ | |_ ___ _ __ | |_ ___
           | | | '_ \| __/ _ \ '_ \| __/ __|
          _| |_| | | | ||  __/ | | | |_\__ \
         |_____|_| |_|\__\___|_| |_|\__|__*/
    private val intentReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleIntent(intent?.extras)
        }
    }
    private fun handleIntent(extras: Bundle?) {

        d(TAG, "handleIntent() {")
        extras?.keySet()?.forEach { key ->
            d(TAG, "    \"$key\": "+extras.get(key))
        }
        d(TAG, "}")

        var intentProfileId = -1
        var intentTargetId = -1

        if (extras?.containsKey("action") == true) {
            val handled = when (extras.getString("action")) {
                "serverMessage" -> {
                    ServerMessageDialog(
                            this,
                            extras.getString("serverMessageTitle") ?: getString(R.string.app_name),
                            extras.getString("serverMessageText") ?: ""
                    )
                    true
                }
                "feedbackMessage" -> {
                    intentTargetId = TARGET_FEEDBACK
                    false
                }
                "userActionRequired" -> {
                    app.userActionManager.execute(
                            this,
                            extras.getInt("profileId"),
                            extras.getInt("type")
                    )
                    true
                }
                "createManualEvent" -> {
                    val date = extras.getString("eventDate")?.let { Date.fromY_m_d(it) } ?: Date.getToday()
                    EventManualDialog(
                            this,
                            App.profileId,
                            defaultDate = date
                    )
                    true
                }
                else -> false
            }
            if (handled && !navLoading) {
                return
            }
        }

        if (extras?.containsKey("reloadProfileId") == true) {
            val reloadProfileId = extras.getInt("reloadProfileId", -1)
            extras.remove("reloadProfileId")
            if (reloadProfileId == -1 || app.profile.id == reloadProfileId) {
                reloadTarget()
                return
            }
        }

        if (extras?.getInt("profileId", -1) != -1) {
            intentProfileId = extras.getInt("profileId", -1)
            extras?.remove("profileId")
        }

        if (extras?.getInt("fragmentId", -1) != -1) {
            intentTargetId = extras.getInt("fragmentId", -1)
            extras?.remove("fragmentId")
        }

        /*if (intentTargetId == -1 && navController.currentDestination?.id == R.id.loadingFragment) {
            intentTargetId = navTarget.id
        }*/

        if (navLoading) {
            b.fragment.removeAllViews()
            if (intentTargetId == -1)
                intentTargetId = HOME_ID
        }

        when {
            app.profile.id == 0 -> {
                if (intentProfileId == -1)
                    intentProfileId = app.config.lastProfileId
                loadProfile(intentProfileId, intentTargetId, extras)
            }
            intentProfileId != -1 -> {
                if (app.profile.id != intentProfileId)
                    loadProfile(intentProfileId, intentTargetId, extras)
                else
                    loadTarget(intentTargetId, extras)
            }
            intentTargetId != -1 -> {
                drawer.currentProfile = app.profile.id
                if (navTargetId != intentTargetId || navLoading)
                    loadTarget(intentTargetId, extras)
            }
            else -> {
                drawer.currentProfile = app.profile.id
            }
        }
        navLoading = false
    }

    override fun recreate() {
        recreate(navTargetId)
    }
    fun recreate(targetId: Int) {
        recreate(targetId, null)
    }
    fun recreate(targetId: Int? = null, arguments: Bundle? = null) {
        val intent = Intent(this, MainActivity::class.java)
        if (arguments != null)
            intent.putExtras(arguments)
        if (targetId != null) {
            intent.putExtra("fragmentId", targetId)
        }
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        startActivity(intent)
    }

    override fun onStart() {
        d(TAG, "Activity started")
        super.onStart()
    }
    override fun onStop() {
        d(TAG, "Activity stopped")
        super.onStop()
    }
    override fun onResume() {
        d(TAG, "Activity resumed")
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_MAIN)
        registerReceiver(intentReceiver, filter)
        EventBus.getDefault().register(this)
        super.onResume()
    }
    override fun onPause() {
        d(TAG, "Activity paused")
        unregisterReceiver(intentReceiver)
        EventBus.getDefault().unregister(this)
        super.onPause()
    }
    override fun onDestroy() {
        d(TAG, "Activity destroyed")
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("fragmentId", navTargetId)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent?.extras)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN_ACTIVITY) {
            if (!app.config.loginFinished)
                finish()
            else {
                handleIntent(data?.extras)
            }
        }
    }

    /*    _                     _                  _   _               _
         | |                   | |                | | | |             | |
         | |     ___   __ _  __| |  _ __ ___   ___| |_| |__   ___   __| |___
         | |    / _ \ / _` |/ _` | | '_ ` _ \ / _ \ __| '_ \ / _ \ / _` / __|
         | |___| (_) | (_| | (_| | | | | | | |  __/ |_| | | | (_) | (_| \__ \
         |______\___/ \__,_|\__,_| |_| |_| |_|\___|\__|_| |_|\___/ \__,_|__*/
    val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.task_open_enter) // new fragment enter
            .setExitAnim(R.anim.task_open_exit) // old fragment exit
            .setPopEnterAnim(R.anim.task_close_enter) // old fragment enter back
            .setPopExitAnim(R.anim.task_close_exit) // new fragment exit
            .build()

    fun loadProfile(id: Int) = loadProfile(id, navTargetId)
    fun loadProfile(id: Int, arguments: Bundle?) = loadProfile(id, navTargetId, arguments)
    fun loadProfile(profile: Profile) = loadProfile(
            profile,
            navTargetId,
            null,
            if (app.profile.archived) app.profile.id else null
    )
    private fun loadProfile(id: Int, drawerSelection: Int, arguments: Bundle? = null) {
        if (App.profileId == id) {
            drawer.currentProfile = app.profile.id
            loadTarget(drawerSelection, arguments)
            return
        }
        val previousArchivedId = if (app.profile.archived) app.profile.id else null
        app.profileLoad(id) {
            loadProfile(it, drawerSelection, arguments, previousArchivedId)
        }
    }
    private fun loadProfile(profile: Profile, drawerSelection: Int, arguments: Bundle?, previousArchivedId: Int?) {
        App.profile = profile
        MessagesFragment.pageSelection = -1

        setDrawerItems()

        if (previousArchivedId != null) {
            // prevents accidentally removing the first item if the archived profile is not shown
            drawer.removeProfileById(previousArchivedId)
        }
        if (profile.archived) {
            drawer.prependProfile(Profile(
                    id = profile.id,
                    loginStoreId = profile.loginStoreId,
                    loginStoreType = profile.loginStoreType,
                    name = profile.name,
                    subname = "Archiwum - ${profile.subname}"
            ).also {
                it.archived = true
            })
        }

        // the drawer profile is updated automatically when the drawer item is clicked
        // update it manually when switching profiles from other source
        //if (drawer.currentProfile != app.profile.id)
        drawer.currentProfile = app.profileId
        loadTarget(drawerSelection, arguments)
    }
    fun loadTarget(id: Int, arguments: Bundle? = null) {
        var loadId = id
        if (loadId == -1) {
            loadId = DRAWER_ITEM_HOME
        }
        val target = navTargetList
                .firstOrNull { it.id == loadId }
        if (target == null) {
            Toast.makeText(this, getString(R.string.error_invalid_fragment, id), Toast.LENGTH_LONG).show()
            loadTarget(navTargetList.first(), arguments)
        }
        else {
            loadTarget(target, arguments)
        }
    }
    private fun loadTarget(target: NavTarget, args: Bundle? = null) {
        d("NavDebug", "loadTarget(target = $target, args = $args)")

        val arguments = args ?: navBackStack.firstOrNull { it.first.id == target.id }?.second ?: Bundle()
        bottomSheet.close()
        bottomSheet.removeAllContextual()
        bottomSheet.toggleGroupEnabled = false
        drawer.close()
        if (drawer.getSelection() != target.id)
            drawer.setSelection(target.id, fireOnClick = false)
        navView.toolbar.setTitle(target.title ?: target.name)
        navView.bottomBar.fabEnable = false
        navView.bottomBar.fabExtended = false
        navView.bottomBar.setFabOnClickListener(null)

        d("NavDebug", "Navigating from ${navTarget.fragmentClass?.java?.simpleName} to ${target.fragmentClass?.java?.simpleName}")

        val fragment = target.fragmentClass?.java?.newInstance() ?: return
        fragment.arguments = arguments
        val transaction = fragmentManager.beginTransaction()

        if (navTarget == target) {
            // just reload the current target
            transaction.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out
            )
        }
        else {
            navBackStack.keys().lastIndexOf(target).let {
                if (it == -1)
                    return@let target
                // pop the back stack up until that target
                transaction.setCustomAnimations(
                        R.anim.task_close_enter,
                        R.anim.task_close_exit
                )

                // navigating grades_add -> grades
                // navTarget == grades_add
                // navBackStack = [home, grades, grades_editor]
                // it == 1
                //
                // navTarget = target
                // remove 1
                // remove 2
                val popCount = navBackStack.size - it
                for (i in 0 until popCount) {
                    navBackStack.removeAt(navBackStack.lastIndex)
                }
                navTarget = target
                navArguments = arguments

                return@let null
            }?.let {
                // target is neither current nor in the back stack
                // so navigate to it
                transaction.setCustomAnimations(
                        R.anim.task_open_enter,
                        R.anim.task_open_exit
                )
                navBackStack.add(navTarget to navArguments)
                navTarget = target
                navArguments = arguments
            }
        }

        if (navTarget.popToHome) {
            // if the current has popToHome, let only home be in the back stack
            // probably `if (navTarget.popToHome)` in popBackStack() is not needed now
            val popCount = navBackStack.size - 1
            for (i in 0 until popCount) {
                navBackStack.removeAt(navBackStack.lastIndex)
            }
        }

        d("NavDebug", "Current fragment ${navTarget.fragmentClass?.java?.simpleName}, pop to home ${navTarget.popToHome}, back stack:")
        navBackStack.forEachIndexed { index, target2 ->
            d("NavDebug", " - $index: ${target2.first.fragmentClass?.java?.simpleName}")
        }

        transaction.replace(R.id.fragment, fragment)
        transaction.commitAllowingStateLoss()

        // TASK DESCRIPTION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val taskDesc = ActivityManager.TaskDescription(
                    if (target.id == HOME_ID) getString(R.string.app_name) else getString(R.string.app_task_format, getString(target.name)),
                    bm,
                    getColorFromAttr(this, R.attr.colorSurface)
            )
            setTaskDescription(taskDesc)
        }

    }
    fun reloadTarget() = loadTarget(navTarget)

    private fun popBackStack(): Boolean {
        if (navBackStack.size == 0) {
            return false
        }
        // TODO back stack argument support
        when {
            navTarget.popToHome -> {
                loadTarget(HOME_ID)
            }
            navTarget.popTo != null -> {
                loadTarget(navTarget.popTo ?: HOME_ID)
            }
            else -> {
                navBackStack.last().let {
                    loadTarget(it.first, it.second)
                }
            }
        }
        return true
    }
    fun navigateUp() {
        if (!popBackStack()) {
            super.onBackPressed()
        }
    }

    /**
     * Use the NavLib's menu button ripple to gain user attention
     * that something has changed in the bottom sheet.
     */
    fun gainAttention() {
        if (app.config.ui.bottomSheetOpened)
            return
        b.navView.postDelayed({
            navView.gainAttentionOnBottomBar()
        }, 2000)
    }

    fun gainAttentionFAB() {
        navView.bottomBar.fabExtended = false

        b.navView.postDelayed({
            navView.bottomBar.fabExtended = true
        }, 1000)

        b.navView.postDelayed({
            navView.bottomBar.fabExtended = false
        }, 3000)
    }

    /*    _____                                _ _
         |  __ \                              (_) |
         | |  | |_ __ __ ___      _____ _ __   _| |_ ___ _ __ ___  ___
         | |  | | '__/ _` \ \ /\ / / _ \ '__| | | __/ _ \ '_ ` _ \/ __|
         | |__| | | | (_| |\ V  V /  __/ |    | | ||  __/ | | | | \__ \
         |_____/|_|  \__,_| \_/\_/ \___|_|    |_|\__\___|_| |_| |_|__*/
    private fun createDrawerItem(target: NavTarget, level: Int = 1): IDrawerItem<*> {
        val item = DrawerPrimaryItem()
                .withIdentifier(target.id.toLong())
                .withName(target.name)
                .withIsHiddenInMiniDrawer(!app.config.ui.miniMenuButtons.contains(target.id))
                .also { if (target.description != null) it.withDescription(target.description!!) }
                .also { if (target.icon != null) it.withIcon(target.icon!!) }
                .also { if (target.title != null) it.withAppTitle(getString(target.title!!)) }
                .also { if (target.badgeTypeId != null) it.withBadgeStyle(drawer.badgeStyle)}
                .withSelectedBackgroundAnimated(false)

        if (target.badgeTypeId != null)
            drawer.addUnreadCounterType(target.badgeTypeId!!, target.id)
        // TODO sub items
        /*
        if (target.subItems != null) {
            for (subItem in target.subItems!!) {
                item.subItems += createDrawerItem(subItem, level+1)
            }
        }*/

        return item
    }

    fun setDrawerItems() {
        d("NavDebug", "setDrawerItems() app.profile = ${app.profile}")
        val drawerItems = arrayListOf<IDrawerItem<*>>()
        val drawerProfiles = arrayListOf<ProfileSettingDrawerItem>()

        val supportedFragments = app.profile.supportedFragments

        targetPopToHomeList.clear()

        var separatorAdded = false

        for (target in navTargetList) {
            if (target.isInDrawer && target.isBelowSeparator && !separatorAdded) {
                separatorAdded = true
                drawerItems += DividerDrawerItem()
            }

            if (target.popToHome)
                targetPopToHomeList += target.id

            if (target.isInDrawer && (target.isStatic || supportedFragments.isEmpty() || supportedFragments.contains(target.id))) {
                drawerItems += createDrawerItem(target)
                if (target.id == 1) {
                    targetHomeId = target.id
                }
            }

            if (target.isInProfileList) {
                drawerProfiles += ProfileSettingDrawerItem()
                        .withIdentifier(target.id.toLong())
                        .withName(target.name)
                        .also { if (target.description != null) it.withDescription(target.description!!) }
                        .also { if (target.icon != null) it.withIcon(target.icon!!) }
            }
        }

        // seems that this cannot be open, because the itemAdapter has Profile items
        // instead of normal Drawer items...
        drawer.profileSelectionClose()

        drawer.setItems(*drawerItems.toTypedArray())
        drawer.removeAllProfileSettings()
        drawer.addProfileSettings(*drawerProfiles.toTypedArray())
    }

    private fun showProfileContextMenu(profile: IProfile, view: View) {
        val profileId = profile.identifier.toInt()
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add(0, 1, 1, R.string.profile_menu_open_settings)
        popupMenu.menu.add(0, 2, 2, R.string.profile_menu_remove)
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == 1) {
                if (profileId != app.profile.id) {
                    loadProfile(profileId, DRAWER_ITEM_SETTINGS)
                    return@setOnMenuItemClickListener true
                }
                loadTarget(DRAWER_ITEM_SETTINGS, null)
            } else if (item.itemId == 2) {
                ProfileRemoveDialog(this, profileId, profile.name?.getText(this) ?: "?")
            }
            true
        }
        popupMenu.show()
    }

    private val targetPopToHomeList = arrayListOf<Int>()
    private var targetHomeId: Int = -1
    override fun onBackPressed() {
        if (!b.navView.onBackPressed()) {
            if (App.config.ui.openDrawerOnBackPressed && ((navTarget.popTo == null && navTarget.popToHome)
                    || navTarget.id == DRAWER_ITEM_HOME)) {
                b.navView.drawer.toggle()
            } else {
                navigateUp()
            }
        }
    }

    fun error(error: ApiError) = errorSnackbar.addError(error).show()
    fun snackbar(text: String, actionText: String? = null, onClick: (() -> Unit)? = null) = mainSnackbar.snackbar(text, actionText, onClick)
    fun snackbarDismiss() = mainSnackbar.dismiss()
}
