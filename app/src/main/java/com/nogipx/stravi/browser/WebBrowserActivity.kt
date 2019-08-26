package com.nogipx.stravi.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nogipx.stravi.R
import com.nogipx.stravi.browser.navigation.ControlWebViewFragment
import com.nogipx.stravi.browser.navigation.ExtensionsListFragment
import com.nogipx.stravi.browser.navigation.SettingsWebViewFragment
import com.nogipx.stravi.browser.navigation.WebExtensionPreferenceFragment
import com.nogipx.stravi.models.LastOpenedTab
import com.nogipx.stravi.models.WebExtension
import com.nogipx.stravi.models.WebTab
import java.net.URL


class WebBrowserActivity
    : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    // Web view
    private lateinit var mWebView: WebView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mWebViewClient: MyWebViewClient
    private lateinit var mChromeClient: MyChromeClient
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    // Bottom sheet
    private lateinit var mBottomSheet: View
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mBottomNavigation: BottomNavigationView

    private lateinit var mPeek: ViewGroup
    private lateinit var mPeekImage: ImageView
    private lateinit var mShadowBackground: ViewGroup


    // Navigation fragments
    private lateinit var mExtensionPreferenceFragment: WebExtensionPreferenceFragment
    private lateinit var mWebControlFragment: ControlWebViewFragment
    private lateinit var mWebSettingsFragment: SettingsWebViewFragment
    private lateinit var mExtensionsListFragment: ExtensionsListFragment


    // Data
    private lateinit var mActiveTab: WebTab

    companion object {
        private const val TAG = "WebBrowserActivity"
        const val EXTRA_PAGE_URL = "$TAG.PAGE_URL"
        const val EXTRA_WEB_EXTENSION_UUID = "$TAG.WEB_EXTENSION_UUID"

        fun createIntent(context: Context, url: URL, extension: WebExtension) : Intent {
            val intent = Intent(context, WebBrowserActivity::class.java)
            intent.putExtra(EXTRA_PAGE_URL, url.toString())
            intent.putExtra(EXTRA_WEB_EXTENSION_UUID, extension.toJson())
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_browser)

        mActiveTab = LastOpenedTab()
            .getUuid(applicationContext)
            .peekTab(applicationContext)

        val tabExtension = mActiveTab.attachedExtension(applicationContext) ?: WebExtension()

        mExtensionPreferenceFragment = WebExtensionPreferenceFragment(tabExtension)
        mExtensionsListFragment = ExtensionsListFragment()
        mWebControlFragment = ControlWebViewFragment()
        mWebSettingsFragment = SettingsWebViewFragment()

        mWebControlFragment.onOpenPage = { openPage(mActiveTab, it) }

        setupBottomSheet()
        setupWebView(mActiveTab)

        mBottomNavigation.selectedItemId = R.id.nav_browser

    }

    override fun onResume() {
        super.onResume()
        openTab(mActiveTab)
    }

    override fun onBackPressed() {
        when {
            mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                hideBottomSheet()

            mWebView.canGoBack() -> mWebView.goBack()

            else -> super.onBackPressed()
        }
    }

    private fun selectNavigation(viewId: Int, fragment: Fragment) {

        val container = R.id.browser_sheet_fragment_container

        supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .addToBackStack(viewId.toString())
            .commit()

        Log.d(TAG, "Select navigation item: $viewId fragment: <${fragment.javaClass}>")
    }

    private fun selectPreviousNavigation() {
        // Find second last back stack name
        // Then select item with that name
        val secondLastEntry = getReverseBackStackEntry(1)

        if (secondLastEntry != null && secondLastEntry.name != null) {

            val itemId = (secondLastEntry.name as String).toInt()
            mBottomNavigation.selectedItemId = itemId
            Log.d(TAG, "Back to $itemId stack entry. BackStack: ${secondLastEntry.id}")
        }
    }

    private fun getReverseBackStackEntry(reverseIndex: Int = 0)
            : FragmentManager.BackStackEntry? {

        // Minus one because it is an index
        val index = supportFragmentManager.backStackEntryCount - reverseIndex - 1

        return if (index >= 0)
            supportFragmentManager.getBackStackEntryAt(index)
        else null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // If selected item id equals second last back stack name
        // Then remove last name from back stack

        val secondLastSelection = getReverseBackStackEntry(1)?.name?.toInt()

        if (secondLastSelection != null && item.itemId == secondLastSelection) {
            Log.d(TAG, "Selected previous navigation item")
            supportFragmentManager.popBackStack()
        }

        return when(item.itemId) {

            R.id.nav_extension -> {
                val tabExtension = mActiveTab.attachedExtension(applicationContext) ?: WebExtension()
                mExtensionPreferenceFragment.extension = tabExtension
                selectNavigation(item.itemId, mExtensionPreferenceFragment)
                true
            }

            R.id.nav_browser -> {
                selectNavigation(item.itemId, mWebControlFragment)
                true
            }

            R.id.nav_browser_settings -> {
                selectNavigation(item.itemId, mWebSettingsFragment)
                true
            }

            R.id.nav_tabs -> {
                selectNavigation(item.itemId, mExtensionsListFragment)
                true
            }

            else -> false

        }
    }

    private fun setupBottomSheet() {

        // Find views
        mBottomSheet = findViewById(R.id.browser_bottom_sheet)
        mPeek = findViewById(R.id.browser_peek_background)
        mPeekImage = findViewById(R.id.browser_peek_image)
        mShadowBackground = findViewById(R.id.shadow_background)
        mBottomNavigation = findViewById(R.id.browser_sheet_navigation)

        // Set defaults
        mPeek.background.alpha = 0
        mBottomNavigation.setOnNavigationItemSelectedListener(this)

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet)
        mBottomSheetBehavior.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                        Log.d(TAG, "Bottom sheet collapsed")
                        hideBottomSheet()
                    }
                }

                override fun onSlide(p0: View, p1: Float) {
                    mPeek.background.alpha = (p1 * 255).toInt()
                    mPeekImage.rotation = 180 * p1
                    mShadowBackground.alpha = p1 / 2

                    if (mShadowBackground.alpha == 0.toFloat())
                        mShadowBackground.visibility = View.GONE
                    else
                        mShadowBackground.visibility = View.VISIBLE
                }
            }
        )

        mShadowBackground.setOnClickListener { hideBottomSheet() }

    }

    fun hideBottomSheet() {

        hideKeyboard(mWebControlFragment.mUrlInput)
        mWebControlFragment.setUrl(mWebView.url)
        mBottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED

        // Add default selection
        mBottomNavigation.selectedItemId = R.id.nav_browser
    }

    private fun setupWebView(initialTab: WebTab = WebTab()) {

        mWebView = findViewById(R.id.browser)
        mProgressBar = findViewById(R.id.browser_progress_bar)
        mSwipeRefreshLayout = findViewById(R.id.browser_swipe_refresh)

        mWebViewClient = MyWebViewClient()
        mChromeClient = MyChromeClient(mProgressBar)

        mSwipeRefreshLayout.setOnRefreshListener(mWebViewClient)
        mWebViewClient.onUrlLoadingCallback = { url ->
            hideBottomSheet()
            openPage(mActiveTab, url)
        }

        mWebViewClient.onRefreshCallback = {
            mSwipeRefreshLayout.isRefreshing = true
            openTab(mActiveTab)
            mSwipeRefreshLayout.isRefreshing = false
        }


        with(mWebView.settings) {

            setAppCachePath(getDir("webview_cache", 0).path)
            setAppCacheEnabled(true)

            javaScriptEnabled = true
            allowUniversalAccessFromFileURLs = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        with(mWebView) {

            webViewClient = mWebViewClient
            webChromeClient = mChromeClient

            if (URLUtil.isValidUrl(initialTab.url))
                loadUrl(initialTab.url)
        }

    }

    private fun openTab(tab: WebTab) = openPage(tab, tab.url)

    private fun openPage(tab: WebTab, urlStr: String = "") : String {

        if (urlStr.isEmpty()) {
            Log.e(TAG, "Prevented try to open WebTab with empty url")
            return ""
        }

        var url = urlStr

        when {
            !urlStr.contains("^[\\w]+://[\\w\\d]+[/]?".toRegex()) -> {
                Log.d(TAG, "Add protocol to shorter address")
                url = "http://$urlStr"
            }

            !URLUtil.isValidUrl(urlStr) -> {
                Log.e(TAG, "Invalid url. Going to web search")
                webSearch(urlStr)
            }
        }

        val extension = tab.attachedExtension(applicationContext)
        if (extension != null)
            mWebViewClient.enqueueFunctionCall(extension.generateJs())


        mWebView.loadUrl(url, mapOf("Content-Security-Policy" to "upgrade-insecure-requests"))

        mActiveTab = updateTab(tab, URL(mWebView.url), mWebView.title)
        LastOpenedTab(tab.uuid).save(applicationContext)

        Log.v(TAG, "Save tab '${tab.title}' as last opened tab")
        showKeyboard(mWebControlFragment.mUrlInput, false)

        return ""

    }

    private fun updateTab(tab: WebTab, url: URL, title: String) : WebTab {

        fun findFirstExtension(host: String) : WebExtension {
            val extensionCandidates = tab.extensionsByHost(applicationContext, host)

            return if (extensionCandidates.isNotEmpty())
                extensionCandidates.first()
            else tab.createExtension(applicationContext)
        }

        if (URL(tab.url).host != url.host) {
            Log.v(TAG, "Host changed, so change extension")

            val newExtension = findFirstExtension(url.host)
            tab.extensionId = newExtension.uuid
            Log.d(TAG, "Change extension to $newExtension")
        }

        tab.url = url.toString()
        tab.title = title

        tab.save(applicationContext)
        return tab

    }

    private fun webSearch(query: String) {

    }

    private fun showKeyboard(view: View, show: Boolean = true) {
        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        when {
            show && !keyboard.isActive ->
                keyboard.showSoftInput(view, 0)

            !show && keyboard.isActive ->
                keyboard.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }

    }

    private fun hideKeyboard(view: View) {
        showKeyboard(view, false)

    }
}
