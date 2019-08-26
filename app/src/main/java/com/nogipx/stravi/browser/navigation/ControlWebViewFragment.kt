package com.nogipx.stravi.browser.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.nogipx.stravi.R

class ControlWebViewFragment : Fragment() {

    lateinit var mUrlInput: TextInputEditText


    var onOpenPage: (t: String) -> String = { "" }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_controls_web_browser, container, false)

        mUrlInput = view.findViewById(R.id.webControl_inputUrl)

        mUrlInput.background = null


        mUrlInput.onFocusChangeListener = View.OnFocusChangeListener { v, focused ->
            if (focused)
                mUrlInput.setSelection(0, mUrlInput.length())
        }


        mUrlInput.setOnEditorActionListener { textView, _, _ ->
            if (textView.text.isNotEmpty())
                onOpenPage(textView.text.toString())

            true
        }

        return view
    }

    fun setUrl(text: String) = mUrlInput.setText(text)
}