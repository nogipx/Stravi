package com.nogipx.stravi.browser

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nogipx.stravi.R
import com.nogipx.stravi.browser.settings.MyChromeClient
import com.nogipx.stravi.browser.settings.MyWebViewClient
import com.nogipx.stravi.browser.viewmodels.WebTabViewModel
import com.nogipx.stravi.gateways.internal_storage.datatypes.LastOpenedTab
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab
import kotlinx.android.synthetic.main.activity_web_browser.*
import kotlinx.android.synthetic.main.bottomsheet_web_browser.*
import java.util.*


class WebBrowserActivity
    : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mWebViewClient: MyWebViewClient
    private lateinit var mChromeClient: MyChromeClient

    // Navigation fragments
    private lateinit var mMainNavigation: NavController

    // Data
    private lateinit var mTabViewModel: WebTabViewModel
    private val navigationStack: ArrayDeque<Int> = ArrayDeque()

    companion object {
        private const val TAG = "WebBrowserActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_browser)

        mMainNavigation = findNavController(R.id.browser_sheet_fragment_nav_host)
        browser_sheet_navigation.setOnNavigationItemSelectedListener(this)

        mWebViewClient = MyWebViewClient()
        mChromeClient = MyChromeClient(browser_progress_bar)
        mTabViewModel = ViewModelProviders.of(this).get(WebTabViewModel::class.java)


        mTabViewModel.openTab(mTabViewModel.getLastOpenedTab())
        mTabViewModel.hideNavigation()

        mTabViewModel.mTab.observe(this, Observer { tab ->

            when(mTabViewModel.state) {

                WebTabViewModel.TabState.OPEN_URL -> {
                    // Generate JS
                    val extension = tab.attachedExtension(applicationContext)
                    if (extension != null)
                        mWebViewClient.enqueueFunctionCall(extension.generateJs())

                    // Load page
                    browser.loadUrl(tab.url, mapOf("Content-Security-Policy" to "upgrade-insecure-requests"))
                    browser_swipe_refresh.isRefreshing = false

                    // Save tab
                    Log.v(TAG, "Save tab '${tab.title}' as last opened tab")
                    LastOpenedTab(tab.uuid).save(applicationContext)

                }

                else -> {}
            }
        })


        mTabViewModel.isActiveNavigation.observe(this, Observer { isActive ->
            if (isActive) {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            } else {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            }
        })

        setupBottomSheet()
        setupWebView(mTabViewModel.getLastOpenedTab())

        browser_sheet_navigation.selectedItemId = R.id.nav_browser

    }

    override fun onBackPressed() {
        when {
            mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {

                if (!mMainNavigation.navigateUp())
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            browser.canGoBack() -> browser.goBack()

            else -> super.onBackPressed()
        }
    }

    private fun setupBottomSheet() {

        browser_peek_background.background.alpha = 0
        shadow_background.setOnClickListener { mTabViewModel.hideNavigation() }


        mBottomSheetBehavior = BottomSheetBehavior.from(browser_bottom_sheet)
        mBottomSheetBehavior.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                        Log.d(TAG, "Bottom sheet collapsed")
                        mTabViewModel.hideNavigation()
                    }
                }

                override fun onSlide(p0: View, p1: Float) {
                    browser_peek_background.background.alpha = (p1 * 255).toInt()
                    browser_peek_image.rotation = 180 * p1
                    shadow_background.alpha = p1 / 2

                    if (shadow_background.alpha == 0.toFloat())
                        shadow_background.visibility = View.GONE
                    else
                        shadow_background.visibility = View.VISIBLE
                }
            }
        )
    }

    private fun setupWebView(initialTab: WebTab = WebTab()) {

        mWebViewClient.onUrlLoadingCallback = { url ->
            mTabViewModel.hideNavigation()
        }

        mWebViewClient.onRefreshCallback = {
            browser_swipe_refresh.isRefreshing = true
            mTabViewModel.openTab(initialTab)
        }


        browser_swipe_refresh.setOnRefreshListener(mWebViewClient)
        with(browser.settings) {

            setAppCachePath(getDir("webview_cache", 0).path)
            setAppCacheEnabled(true)

            javaScriptEnabled = true
            allowUniversalAccessFromFileURLs = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        with(browser) {

            webViewClient = mWebViewClient
            webChromeClient = mChromeClient

            if (URLUtil.isValidUrl(initialTab.url))
                loadUrl(initialTab.url)
        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.nav_extension ->
                mMainNavigation.navigate(R.id.webExtensionPreferenceFragment)

            R.id.nav_browser ->
                mMainNavigation.navigate(R.id.controlWebViewFragment)

            R.id.nav_browser_settings ->
                mMainNavigation.navigate(R.id.settingsWebViewFragment)
        }

        return true
    }
}
