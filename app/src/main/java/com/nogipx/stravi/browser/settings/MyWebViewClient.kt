package com.nogipx.stravi.browser.settings

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nogipx.stravi.jsgenerator.FunctionJS
import java.net.URL
import java.util.*

class MyWebViewClient : WebViewClient(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val TAG = "MyWebViewClient"
    }

    private lateinit var mWebView: WebView
    private val invokeQueue = ArrayDeque<FunctionJS>()
    val disallowCrossDomainHref = false

    var onUrlLoadingCallback: (url: String) -> Unit = {}
    var onRefreshCallback: () -> Unit = {}


    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        mWebView = view!!
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        for ((i, function) in invokeQueue.withIndex()) {

            Log.d(TAG, "Invoke #$i: \n${function.iife()}")

            view!!.evaluateJavascript(function.iife()) {
                Log.d(TAG, "Evaluated: $function")
            }

            invokeQueue.remove()

            Log.d(TAG, "Dequeue size: ${invokeQueue.size}")

        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val requestUrl = request!!.url
        val isSimilarDomain = URL(view!!.url).host == requestUrl.host

        return when {
            disallowCrossDomainHref && !isSimilarDomain -> {
                Log.d(TAG, "Disallow forward to '${requestUrl.host}'")
                true
            }

            else -> {
                Log.d(TAG, "Allow forward to '${requestUrl.host}'")
                onUrlLoadingCallback(requestUrl.toString())
                false
            }
        }
    }

    override fun onRefresh() {
        onRefreshCallback()
    }

    fun enqueueFunctionCall(code: String) =
        invokeQueue.add(FunctionJS(code = code))

    fun enqueueFunctionCall(function: FunctionJS) =
        invokeQueue.add(function)

    fun enqueueFunctionCall(functions: List<FunctionJS>) {
        for (function in functions)
            invokeQueue.add(function)
    }
}