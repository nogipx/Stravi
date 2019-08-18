package com.nogipx.stravi.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.nogipx.stravi.CustomPlayerWebViewClient
import com.nogipx.stravi.R
import com.nogipx.stravi.jsgenerator.MyVisibilityJsGenerator
import com.nogipx.stravi.models.WebExtension
import java.net.URL


class WebPageActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private lateinit var mWebViewClient: WebViewClient

    companion object {
        private const val TAG = "WebPageActivity"
        const val EXTRA_PAGE_URL = "$TAG.PAGE_URL"
        const val EXTRA_WEB_EXTENSION_JSON = "$TAG.WEB_EXTENSION_JSON"

        fun createIntent(context: Context, url: URL, extension: WebExtension) : Intent {
            val intent = Intent(context, WebPageActivity::class.java)
            intent.putExtra(EXTRA_PAGE_URL, url.toString())
            intent.putExtra(EXTRA_WEB_EXTENSION_JSON, extension.toJson())
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_player)

        mWebView = findViewById(R.id.playerWebView)

        val url = intent.getStringExtra(EXTRA_PAGE_URL)

        Log.v(TAG, "Open url: $url")
        val extension = WebExtension().fromJson<WebExtension>(intent.getStringExtra(EXTRA_WEB_EXTENSION_JSON)!!)

        val jse = MyVisibilityJsGenerator(targetsSelector = extension.targets.joinToString())

        if (extension.css.isNotEmpty()) jse.addCSS(extension.css)

        mWebViewClient = CustomPlayerWebViewClient(invokeJs = listOf(jse.generate()))

        with(mWebView.settings) {
            javaScriptEnabled = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        with(mWebView) {
            webViewClient = mWebViewClient
            loadUrl(url)
        }

    }
}
