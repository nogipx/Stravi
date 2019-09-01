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
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nogipx.stravi.R
import com.nogipx.stravi.browser.settings.MyChromeClient
import com.nogipx.stravi.browser.settings.MyWebViewClient
import com.nogipx.stravi.browser.viewmodels.WebTabViewModel
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab
import kotlinx.android.synthetic.main.activity_web_browser.*
import kotlinx.android.synthetic.main.bottomsheet_web_browser.*
import java.net.URL


class WebBrowserActivity
    : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mWebViewClient: MyWebViewClient
    private lateinit var mChromeClient: MyChromeClient

    // Navigation fragments
    private lateinit var mMainNavigation: NavController

    // Data
    private lateinit var model: WebTabViewModel

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
        model = ViewModelProviders.of(this)[WebTabViewModel::class.java]


        val lastTab = model.getLastOpenedTab()

        model.setTab(lastTab)
        model.openUrl(URL(lastTab.url))
        model.hideNavigation()

        setupModelObservers(model)
        setupBottomSheet()
        setupWebView(model.getLastOpenedTab())

        browser_sheet_navigation.selectedItemId = R.id.nav_browser
    }

    private fun setupModelObservers(model: WebTabViewModel) {

        model.mTab.observe(this, Observer { tab ->

            // Generate JS
            val extension = model.mExtension.value
            if (extension != null && extension.active) {
                mWebViewClient.enqueueFunctionCall(extension.generatePayload())
                Log.d(TAG, "Proceed extension $extension because it's active")
            }

            model.hideNavigation()

            // Load page
            browser.loadUrl(tab.url, mapOf("Content-Security-Policy" to "upgrade-insecure-requests"))
            browser_swipe_refresh.isRefreshing = false

            // Save tab
            Log.v(TAG, "Save tab '${tab.title}' as last opened tab")
            model.setLastOpenedTab(tab)

        })

        model.isActiveNavigation.observe(this, Observer { isActive ->
            if (isActive) {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            } else {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val options = NavOptions.Builder()
                options.setLaunchSingleTop(true)
                mMainNavigation.navigate(R.id.controlWebViewFragment)
            }
        })
    }

    override fun onBackPressed() {
        when {
            mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
                val options = NavOptions.Builder()
                options.setLaunchSingleTop(true)
                if (!mMainNavigation.navigateUp())
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            browser.canGoBack() -> browser.goBack()

            else -> super.onBackPressed()
        }
    }

    private fun setupBottomSheet() {

        browser_peek.background.alpha = 0
        shadow_background.setOnClickListener { model.hideNavigation() }


        mBottomSheetBehavior = BottomSheetBehavior.from(browser_bottom_sheet)
        mBottomSheetBehavior.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_COLLAPSED) {
                        Log.d(TAG, "Bottom sheet collapsed")
                        model.hideNavigation()
                    }
                }

                override fun onSlide(p0: View, p1: Float) {
                    browser_peek.background.alpha = (p1 * 255).toInt()
//                    browser_peek_image.rotation = 180 * p1
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
            // TODO Issue: not updates hyper link forwards
            model.openUrl(URL(url))
        }

        mWebViewClient.onRefreshCallback = {
            browser_swipe_refresh.isRefreshing = true
            val openedTab = model.mTab.value
            if (openedTab != null) model.openUrl(URL(openedTab.url))
        }

        browser_swipe_refresh.setColorSchemeResources(R.color.colorAccent)
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

            R.id.nav_browser ->
                mMainNavigation.navigate(R.id.controlWebViewFragment)

            R.id.nav_browser_settings ->
                mMainNavigation.navigate(R.id.settingsWebViewFragment)

            R.id.nav_tabs ->
                mMainNavigation.navigate(R.id.extensionsListFragment)
        }

        return true
    }
}