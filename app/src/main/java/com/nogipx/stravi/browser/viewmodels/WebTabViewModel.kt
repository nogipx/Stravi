package com.nogipx.stravi.browser.viewmodels

import android.app.Application
import android.util.Log
import android.webkit.URLUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nogipx.stravi.gateways.internal_storage.datatypes.LastOpenedTab
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab
import java.net.URL

class WebTabViewModel (application: Application) : AndroidViewModel(application) {

    var mTab: MutableLiveData<WebTab> = MutableLiveData()
    var mExtension: MutableLiveData<WebExtension> = MutableLiveData()

    var isActiveNavigation: MutableLiveData<Boolean> = MutableLiveData()

    companion object {
        const val TAG = "WebTabViewModel"
    }

    var state = TabState.INITIAL

    enum class TabState {
        INITIAL, RESTORE_TAB, OPEN_NEW_TAB, OPEN_URL, UPDATE_EXTENSION
    }


    fun openTab(uuid: String) : WebTab {
        state = TabState.OPEN_URL
        val tab = WebTab().get<WebTab>(getApplication(), uuid)!!

        Log.v(TAG, "Open tab '${tab.title}'")
        mTab.value = tab

        return tab
    }

    fun openTab(tab: WebTab) {
        state = TabState.OPEN_URL
        mTab.value = tab
    }

    /**
     * Creates new WebTab without WebExtension
     * Sets last change as 'create_tab'
     */
    fun openNewTab() : WebTab {
        state = TabState.OPEN_NEW_TAB
        val tab = WebTab()

        mTab.value = tab

        return tab
    }

    /**
     * Updates WebTab when new url has opened
     * Finds last (for now) WebExtension by host
     * and attaches it to the WebTab
     *
     * Firstly checks if host has changed
     * then finds last (for now) extension candidate.
     *
     * If WebExtension candidate is not empty
     * then attaches it to WebTab
     * else attaches null
     */
    fun openUrl(url: URL, title: String = "") : WebTab {
        state = TabState.OPEN_URL

        val tab = mTab.value!!

        val tabUrl =
            if (tab.url.isNotEmpty() && URLUtil.isValidUrl(tab.url))
                URL(tab.url)
            else null

        if (tabUrl != null && url.host != tabUrl.host) {

            val extensionCandidate = WebExtension().extensionsByHost(getApplication(), url.host).last()
            Log.d(TAG, "Open url extension candidate: " +
                    "IsEmpty -> ${extensionCandidate.isEmpty()}: " +
                    "$extensionCandidate")

            if (extensionCandidate.isNotEmpty())
                mExtension.value = extensionCandidate
            else
                mExtension.value = null

        }

        tab.url = url.toString()
        tab.title = title

        mTab.value = tab
        return tab
    }

    fun getLastOpenedTab() : WebTab {
        val tab = LastOpenedTab().getUuid(getApplication()).peekTab(getApplication())
        Log.d(TAG, "Peek last opened tab")
        mTab.value = tab
        return tab
    }

    fun attachExtension(uuid: String) {

    }

    fun showNavigation() { isActiveNavigation.value = true }
    fun hideNavigation() { isActiveNavigation.value = false }


}