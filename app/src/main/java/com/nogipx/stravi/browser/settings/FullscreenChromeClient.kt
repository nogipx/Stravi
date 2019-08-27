package com.nogipx.stravi.browser.settings

import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.FrameLayout

class FullscreenChromeClient(
    private val parent: ViewGroup,
    private val content: ViewGroup)
    : WebChromeClient() {

    var customView: View? = null

    private val matchParentLayout = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        customView = view
        view!!.layoutParams = matchParentLayout
        parent.addView(view)
        content.visibility = View.GONE
    }

    override fun onHideCustomView() {
        content.visibility = View.VISIBLE
        parent.removeView(customView)
        customView = null
    }
}