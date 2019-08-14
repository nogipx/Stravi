package com.nogipx.stravi.activities

import android.app.Fragment
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.nogipx.stravi.R

class PageSettingsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "activities.CreateWebPage"
        const val EXTRA_PAGE = "$TAG.EXTRA_PAGE"
    }

    private val mExtensionsFragment: Fragment = ExtensionsListFragment()

    private lateinit var mUrlInput: EditText
    private lateinit var mLabelInput: EditText
    private lateinit var mSelectExtensonButton: Button
    private lateinit var mSelectionFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_web_page)

        mSelectExtensonButton = findViewById(R.id.pageSettings_createPage)
        mSelectionFragment = findViewById(R.id.pageSettings_selectExtensionFragment)

        mSelectionFragment.fragmentManager!!.beginTransaction()
            .attach(mExtensionsFragment)
            .commit()
    }
}
