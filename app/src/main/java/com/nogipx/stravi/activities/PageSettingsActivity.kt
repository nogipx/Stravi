package com.nogipx.stravi.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputLayout
import com.nogipx.stravi.R
import com.nogipx.stravi.fragments.ExtensionsListFragment
import java.lang.Exception
import java.net.URL

class PageSettingsActivity : FragmentActivity() {

    companion object {
        private const val TAG = "activity.CreateWebPage"
        const val EXTRA_PAGE = "$TAG.EXTRA_PAGE"
    }

    private lateinit var mExtensionsFragment: ExtensionsListFragment

    private lateinit var mUrlInput: EditText
    private lateinit var mUrlInputWrapper: TextInputLayout
    private lateinit var mLabelInput: EditText
    private lateinit var mSubmitButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_web_page)

        // Setup views
        mExtensionsFragment = ExtensionsListFragment()
        mUrlInput = findViewById(R.id.pageSettings_inputUrl)
        mUrlInputWrapper = findViewById(R.id.pageSettings_inputUrlWrapper)
        mSubmitButton = findViewById(R.id.pageSettings_submit)


        mUrlInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                try {
                    val domain = URL(text).host
                    Log.d(TAG, "Domain is $domain")
                    mUrlInputWrapper.error = ""
                    mExtensionsFragment.mAdapter.filterByDomain(domain)

                } catch (e: Exception) {
                    mUrlInputWrapper.error = "Not valid address"
                    Log.e(TAG, e.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        mSubmitButton.setOnClickListener {
            Log.d(TAG, "Selected extension: ${mExtensionsFragment.mAdapter.mTracker?.selection}")
        }


        supportFragmentManager.beginTransaction()
            .add(R.id.pageSettings_extensionsFragmentContainer, mExtensionsFragment)
            .commit()
    }
}
