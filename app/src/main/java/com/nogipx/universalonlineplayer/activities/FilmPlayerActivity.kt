package com.nogipx.universalonlineplayer.activities

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.nogipx.universalonlineplayer.CustomPlayerWebViewClient
import com.nogipx.universalonlineplayer.MyVisibilityJsGenerator
import com.nogipx.universalonlineplayer.R
import java.net.URL


class FilmPlayerActivity : Activity() {

    private var playerWebView: WebView? = null
    private var playerWebViewClient: WebViewClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_player)

        playerWebView = findViewById(R.id.playerWebView)

//        val pageUrl = URL("http://mega-mult.ru/serii/525-avatar-legenda-ob-aange-i-korre.html")
//        val jse = MyVisibilityJsGenerator(showSelectors = listOf("#movie_area",".vibor"))
//        jse.addCSS("""
//            ${jse.target(0)} iframe {
//                width: 100vw;
//            }
//        """)

        val pageUrl = URL("https://online.animedia.tv/anime/mastera-mecha-onlayn/1/1")
        val jse = MyVisibilityJsGenerator(targetsSelectors = listOf("#player__jw", "#season-search", "#playlist__jw"))
        val mixin = """
            display: block;
            position: absolute;
            left: 0;
            width: 100vw;
        """.trimIndent()

        jse.addCSS("""
            .main-page {min-width: 100vw;}
            ${jse.target(0)} #player, #oframeplayer {width: 100vw;}
            ${jse.target(0)} .post-player__controls {display: none;}
            ${jse.target(2)} {$mixin}
        """)

//        val pageUrl = URL("https://m.twitch.tv/lorinefairy")
//        val jse = MyVisibilityJsGenerator(showSelectors = listOf(".player-layout__player", ".chat-pane"))

//        val pageUrl = URL("http://animevost.club/online/legenda_o_grankreste_2018_720_hd/28-1-0-564")
//        val jse = MyVisibilityJsGenerator(showSelectors = listOf(".viboom-overroll iframe"))

//        val pageUrl = URL("https://yummyanime.club/catalog/item/11-glaz")
//        val jse = MyVisibilityJsGenerator(showSelectors = listOf("#video"))

        playerWebViewClient = CustomPlayerWebViewClient(invokeJs = listOf(jse.generate()))

        with(playerWebView!!.settings) {
            javaScriptEnabled = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
        }

        with(playerWebView!!) {
            webViewClient = playerWebViewClient
            loadUrl(pageUrl.toString())
        }

    }
}
