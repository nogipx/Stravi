package com.nogipx.stravi.browser.viewmodels

import android.app.Application
import android.util.Log
import android.webkit.URLUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nogipx.stravi.gateways.internal_storage.datatypes.LastOpenedTab
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab
import java.net.URL

class WebTabViewModel (application: Application) : AndroidViewModel(application) {

    private val _mTab: MutableLiveData<WebTab> = MutableLiveData()
    val mTab: LiveData<WebTab> get() = _mTab

    private val _mExtension: MutableLiveData<WebExtension> = MutableLiveData()
    val mExtension: LiveData<WebExtension> get() = _mExtension

    private val _isActiveNavigation: MutableLiveData<Boolean> = MutableLiveData()
    val isActiveNavigation: LiveData<Boolean> get() = _isActiveNavigation


    companion object {
        const val TAG = "WebTabViewModel"
    }


    /**
     * Creates new WebTab without WebExtension
     * Sets last change as 'create_tab'
     */
    fun openNewTab() : WebTab {
        val tab = WebTab()
        _mTab.value = tab

        return tab
    }

    /**
     * Updates WebTab when new url has opened
     * Finds last (for now) WebExtension by host
     * and attaches it to the WebTab
     *
     * Finds list of extension candidates.
     * If list is not empty
     * then attaches first candidate to WebTab
     * else attaches null
     */
    fun openUrl(url: URL) : WebTab {

        val tab = mTab.value!!

        val tabUrl =
            if (tab.url.isNotEmpty() && URLUtil.isValidUrl(tab.url))
                URL(tab.url)
            else null


        var extension: WebExtension? = null
        if (tabUrl != null) {

            val extensionCandidates = WebExtension().extensionsByHost(getApplication(), url.host)

            if (extensionCandidates.isNotEmpty()) {
                val candidate = extensionCandidates.first()

                Log.v(TAG, "Open url extension candidate: " +
                            "IsEmpty -> ${candidate.isEmpty()}: " +
                            "$candidate"
                )

                if (candidate.isNotEmpty())
                    extension = candidate

            } else Log.e(TAG, "There is not an extension for host '${url.host}'")
        }

        tab.url = url.toString()
        tab.title = url.host

        tab.save(getApplication())
        setLastOpenedTab(tab)
        _mTab.value = tab
        _mExtension.value = extension

        return tab
    }

    fun refreshTab() {
        mTab.value?.let {
            openUrl(URL(it.url))
        }
    }

    fun setTitle(title: String) {
        val tab = mTab.value

        tab?.apply {
            this.title = title
            save(getApplication())
        }

        _mTab.value = tab
    }

    fun getLastOpenedTab() : WebTab {
        val tab = LastOpenedTab().getUuid(getApplication()).peekTab(getApplication())
        Log.d(TAG, "Peek last opened tab")
        return tab
    }

    fun setLastOpenedTab(tab: WebTab) =
        LastOpenedTab(tab.uuid).save(getApplication())

    fun toggleExtensionActive() {
        val extension = mExtension.value

        _mExtension.value = extension?.apply {
            active = !active
            save(getApplication())
        }
    }

    fun getOrCreateExtension() : WebExtension? =
        mExtension.value ?: createExtension().apply { _mExtension.value = this }


    private fun createExtension() : WebExtension? =
        mTab.value?.run {
            val url = this.url
            if (!URLUtil.isValidUrl(url)) {
                Log.e(TAG, "Cannot create extension for invalid url")
                return null
            }
            val host = URL(mTab.value?.url).host
            val extension = WebExtension(
                active = false,
                name = url,
                host = host
            )
            extension.save(getApplication())
            return extension
        }

    fun setExtension(extension: WebExtension) { _mExtension.value = extension }
    fun setTab(tab: WebTab) { _mTab.value = tab }

    fun showNavigation() { _isActiveNavigation.value = true }
    fun hideNavigation() { _isActiveNavigation.value = false }


}