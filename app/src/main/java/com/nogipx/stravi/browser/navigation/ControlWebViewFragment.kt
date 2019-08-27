package com.nogipx.stravi.browser.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.nogipx.stravi.R
import com.nogipx.stravi.browser.viewmodels.WebTabViewModel
import kotlinx.android.synthetic.main.fragment_controls_web_browser.*
import kotlinx.android.synthetic.main.fragment_controls_web_browser.view.*
import java.net.URL

class ControlWebViewFragment : Fragment() {

    private lateinit var model: WebTabViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_controls_web_browser, container, false)
        model = ViewModelProviders.of(activity!!)[WebTabViewModel::class.java]

        view.webControl_inputUrl.onFocusChangeListener = View.OnFocusChangeListener { v, focused ->
            if (focused)
                webControl_inputUrl.selectAll()
        }

        view.webControl_inputUrl.setOnEditorActionListener { textView, _, _ ->
            val text = textView.text.toString()

            if (text.isNotEmpty() && URLUtil.isValidUrl(text)) {
                webControl_inputUrl.setText(text)
                showKeyboard(view = view.webControl_inputUrl, show = false)
                model.openUrl(URL(text))
            }
            true
        }

        return view
    }

    private fun showKeyboard(
        context: Context = activity!!,
        view: View = webControl_inputUrl,
        show: Boolean = true) {

        val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        when {
            show && !keyboard.isActive ->
                keyboard.showSoftInput(view, 0)

            !show && keyboard.isActive ->
                keyboard.hideSoftInputFromWindow(view.applicationWindowToken, 0)
        }

    }
}