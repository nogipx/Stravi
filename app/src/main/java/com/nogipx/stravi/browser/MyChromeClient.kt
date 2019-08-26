package com.nogipx.stravi.browser

import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar

class MyChromeClient(
    private val mProgressBar: ProgressBar
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        mProgressBar.progress = newProgress
        if (newProgress < 100)
            mProgressBar.visibility = View.VISIBLE
        else
            mProgressBar.visibility = View.INVISIBLE
    }

}