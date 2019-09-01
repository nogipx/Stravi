package com.nogipx.stravi.browser.navigation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.nogipx.stravi.R
import com.nogipx.stravi.browser.ViewModelListener
import com.nogipx.stravi.browser.viewmodels.WebTabViewModel
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebExtension
import com.nogipx.stravi.gateways.internal_storage.datatypes.WebTab
import kotlinx.android.synthetic.main.extension_controls.view.*
import kotlinx.android.synthetic.main.fragment_controls_web_browser.view.*
import java.net.URL

class ControlWebViewFragment : Fragment(){

    private lateinit var navMenu: Menu
    private lateinit var model: WebTabViewModel
    private lateinit var mCodeEditorFragment: Fragment
    private lateinit var mView: View


    companion object {
        const val TAG = "ControlWebViewFragment"
    }

    var defaultTint = R.color.control_default_tint
    var contentTint = R.color.colorPrimaryDark
    var noContentTint = R.color.colorAccent
    var enabledTint = R.color.colorPrimary


    private lateinit var mUrlInputCache: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_controls_web_browser, container, false)
        setHasOptionsMenu(true)

        model = ViewModelProviders.of(activity!!)[WebTabViewModel::class.java]

        setupModelObservers(view, model)
        setupUrlInput(view)
        setupExtensionControl(view)

        mView = view
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        navMenu = menu!!
    }

    private fun setupModelObservers(root: View, model: WebTabViewModel) {

        // Tinting extension control according its state
        val mExtensionObserver = Observer<WebExtension> {

            root.control_extension_settings.visibility = View.GONE
            root.control_extension_js.visibility = View.GONE

            if (it == null) {
                val icons = listOf(
                    root.control_extension_js,
                    root.control_extension_settings,
                    root.control_extension_toggleActive
                )

                icons.forEach { icon -> configIcon(icon, defaultTint, alpha = 128, enable = false) }
                return@Observer

            } else configIcon(root.control_extension_settings, defaultTint)


            val cssColor = if (it.css.isNotEmpty()) contentTint else noContentTint
            val toggleColor = if (it.active) enabledTint else noContentTint

            configIcon(root.control_extension_css, cssColor)
            configIcon(root.control_extension_toggleActive, toggleColor)

            Log.d(TAG, "Extension $it set ${if (it.active) "active" else "inactive" }")
        }

        val mTabObserver = Observer<WebTab> { tab ->
            root.webControl_inputUrl.setText(tab.url)
        }

        val mNavigationObserver = Observer<Boolean> { isActive ->
            if (!isActive) {
                root.webControl_inputUrl.run {
                    setText(model.mTab.value?.url)
                    hideKeyboard()
                }
            }
        }

        // Maybe I should manually delete them,
        // but at least it works without perfomance issues
        listOf(
            ViewModelListener(this, model.mTab, listOf(mTabObserver)),
            ViewModelListener(this, model.mExtension, listOf(mExtensionObserver)),
            ViewModelListener(this, model.isActiveNavigation, listOf(mNavigationObserver))
        ).forEach { lifecycle.addObserver(it) }

    }

    private fun setupExtensionControl(view: View) {
        view.control_extension_toggleActive.setOnClickListener {
            model.toggleExtensionActive()
            model.hideNavigation()
            model.refreshTab()
        }

        view.control_extension_css.setOnClickListener {
            model.getOrCreateExtension()?.run {

                val input = TextInputEditText(activity)
                input.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                input.setText(this.css)

                AlertDialog.Builder(activity)
                    .setView(input)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        this.css = input.text.toString()
                        this.save(context!!)
                        model.hideNavigation()
                        model.refreshTab()
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->}
                    .create()
                    .show()
            }
        }

        view.control_extension_settings.setOnClickListener {
            model.mExtension.value?.run {
                val preference = WebExtensionPreferenceFragment(this)
            }
        }
    }

    private fun setupUrlInput(root: View) {
        val mInput = root.webControl_inputUrl

        mInput.setOnFocusChangeListener { _, focused ->
            val text = mInput.text.toString()
            when {
                focused -> {
                    mUrlInputCache = text
                    mInput.setText("https://")
                }

                !focused && text.isEmpty() -> {
                    mInput.setText(mUrlInputCache)
                    mUrlInputCache = ""
                }
            }
        }

        mInput.setOnEditorActionListener { textView, _, _ ->
            val text = textView.text.toString()

            if (text.isNotEmpty() && URLUtil.isValidUrl(text)) {
                with(mInput) {
                    setText(text)
                    hideKeyboard()
                }
                model.openUrl(URL(text))
            }
            true
        }
    }

    private fun configIcon(image: ImageView, color: Int, alpha: Int = 255, enable: Boolean = true) {
        val colors = ContextCompat.getColorStateList(activity!!, color)
        val d = DrawableCompat.wrap(image.drawable)
        d.alpha = alpha
        d.setTintList(colors)
        image.setImageDrawable(d)
        image.isEnabled = enable
    }


    private fun View.hideKeyboard() {
        val keyboard = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(this.applicationWindowToken, 0)
    }
}