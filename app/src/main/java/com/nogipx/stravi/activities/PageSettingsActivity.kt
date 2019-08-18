package com.nogipx.stravi.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.webkit.URLUtil
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nogipx.stravi.R
import com.nogipx.stravi.fragments.ExtensionsListFragment
import com.nogipx.stravi.models.WebPage
import java.net.URL

class PageSettingsActivity : AppCompatActivity() {

    private var mPage: WebPage = WebPage()

    companion object {
        private const val TAG = "PageSettingsActivity"
        const val EXTRA_PAGE = "$TAG.EXTRA_PAGE"

        fun createIntent(context: Context, page: WebPage) : Intent {
            val intent = Intent(context, PageSettingsActivity::class.java)
            intent.putExtra(EXTRA_PAGE, page.toJson())
            return intent
        }
    }

    /* Views */

    private lateinit var mExtensionsFragment: ExtensionsListFragment

    private lateinit var mInputUrl: TextInputEditText
    private lateinit var mInputUrlLayout: TextInputLayout

    private lateinit var mInputLabel: TextInputEditText
    private lateinit var mInputLabelLayout: TextInputLayout

    private lateinit var mButtonCancel: Button
    private lateinit var mButtonSave: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_web_page)

        // Get extras from intent
        val extraPage = intent.getStringExtra(EXTRA_PAGE)


        // Handle extras
        if (extraPage != null)
            mPage = WebPage().fromJson(intent.getStringExtra(EXTRA_PAGE)!!)


        // Find views
        mExtensionsFragment = ExtensionsListFragment()

        mInputUrl = findViewById(R.id.pageSettings_inputUrl)
        mInputUrlLayout = findViewById(R.id.pageSettings_inputUrlLayout)

        mInputLabel = findViewById(R.id.pageSettings_inputLabel)
        mInputLabelLayout = findViewById(R.id.pageSettings_inputLabelLayout)

        mButtonSave = findViewById(R.id.pageSettings_save)
        mButtonCancel = findViewById(R.id.pageSettings_cancel)


        // Fill views with extra's data
        mInputUrl.setText(mPage.url)
        mInputLabel.setText(mPage.label)


        // Listeners
        mInputUrl.addTextChangedListener(afterTextChangedWatcher {
            val text = it.toString()

            when {
                URLUtil.isValidUrl(text) -> {
                    mInputUrlLayout.error = ""

                    val host = URL(text).host
                    mExtensionsFragment.mAdapter.filterByHost(host)
                    Log.i(TAG, "Filtered extensions by '$host' host")
                }

                text.isEmpty() ->
                    mInputUrlLayout.error = ""

                else -> {
                    val e = "Not valid address"
                    mInputUrlLayout.error = e

                }
            }
        })

        mButtonCancel.setOnClickListener { finish() }
        mButtonSave.setOnClickListener { onBackPressed() }

        supportFragmentManager.beginTransaction()
            .add(R.id.pageSettings_extensionsFragmentContainer, mExtensionsFragment)
            .commit()
    }

    private fun afterTextChangedWatcher(afterTextChanged: (editable: Editable?) -> Unit) : TextWatcher =
        object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) { afterTextChanged(p0) }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        }

    fun getResult() : WebPage {
        if (isValidInput()) {
            mPage.label = mInputLabel.text.toString()
            mPage.url = mInputUrl.text.toString()

            mPage.extensionId = mExtensionsFragment.mAdapter.selectedExtension!!.uuid

            Toast.makeText(baseContext, "Updated", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "$mPage has updated")
        }
        return mPage
    }

    fun isValidInput() : Boolean {
        val dur = Toast.LENGTH_SHORT
        return when {
            mInputLabel.text.toString().isEmpty() -> {
                Toast.makeText(this, "Please input label", dur).show()
                false
            }
            !URLUtil.isValidUrl(mInputUrl.text.toString()) -> {
                Toast.makeText(this, "Invalid site address", dur).show()
                false
            }
            mExtensionsFragment.mAdapter.selectedExtension == null -> {
                Toast.makeText(this, "Please select extension", dur).show()
                false
            }
            else -> true
        }
    }

    override fun onBackPressed() {
        if (isValidInput()) {
            val data = createIntent(this, getResult())
            setResult(Activity.RESULT_OK, data)

            Log.i(TAG, "$mPage has created.")
            finish()
        }
    }
}
