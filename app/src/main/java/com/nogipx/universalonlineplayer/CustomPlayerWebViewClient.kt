package com.nogipx.universalonlineplayer

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nogipx.universalonlineplayer.jsgenrator.FunctionJS
import org.slf4j.LoggerFactory
import java.net.URL

class CustomPlayerWebViewClient(
    private val invokeJs: List<FunctionJS> = listOf())
    : WebViewClient() {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Hide not using elements in DOM.
     * Show target elements
     */

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        for (method in invokeJs) {
            view!!.evaluateJavascript(
                method.iife(),
                {log.debug("@ Executed JS @\n${method.iife()}\n") }
            )
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return URL(view!!.url).host != request!!.url.host
    }
}