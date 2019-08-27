package com.nogipx.stravi.browser.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.preference.*
import com.nogipx.stravi.R
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension

class WebExtensionPreferenceFragment(
    var extension: WebExtension = WebExtension()
)
    : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener{

    lateinit var mIsActive: SwitchPreference
    lateinit var mName: EditTextPreference
    lateinit var mHost: EditTextPreference
    lateinit var mTargets: EditTextPreference
    lateinit var mCustomCss: EditTextPreference
    lateinit var mActions: ListPreference


    companion object {
        const val TAG = "ExtensionPrefFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        val scrollView = NestedScrollView(context!!)
        scrollView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        scrollView.addView(view)

        return scrollView
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_extension, rootKey)

        mIsActive = preferenceManager.findPreference("isActive") as SwitchPreference
        mName = preferenceManager.findPreference("name") as EditTextPreference
        mHost = preferenceManager.findPreference("host") as EditTextPreference
        mTargets = preferenceManager.findPreference("targets") as EditTextPreference
        mCustomCss = preferenceManager.findPreference("customCss")as EditTextPreference
        mActions = preferenceManager.findPreference("actions") as ListPreference


        if (extension.isNotEmpty()) {
            when(extension.active) {
                true -> mIsActive.switchTextOn
                false -> mIsActive.switchTextOff
            }

            mName.text = extension.name
            mName.summary = extension.name

            mHost.text = extension.host
            mHost.summary = extension.host

            mTargets.text = extension.targets.joinToString()
            mTargets.summary = extension.targets.joinToString()

            mCustomCss.text = extension.css
        }

        mIsActive.onPreferenceChangeListener = this
        mName.onPreferenceChangeListener = this
        mHost.onPreferenceChangeListener = this
        mTargets.onPreferenceChangeListener = this
        mCustomCss.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {

        when (preference?.key) {
            "isActive" ->  extension.active = newValue as Boolean
            "name" -> extension.name = newValue as String
            "host" -> extension.host = newValue as String
            "targets" -> extension.targets = (newValue as String).split(",")
            "customCss" -> extension.css = newValue as String
        }

        Log.d(TAG, "Updated values: $extension")
        return true
    }

    override fun onPause() {
        super.onPause()
        if (extension.isNotEmpty()) {
            extension.save(activity!!.applicationContext)
        }
    }

    fun removePreference(key: String) {
        val preference = preferenceManager.findPreference(key)
        preferenceScreen.removePreference(preference)
    }
}
