package com.nogipx.stravi

import com.nogipx.stravi.models.WebExtension
import com.nogipx.stravi.models.WebPage

val extensions = mapOf(
    "mega-mult.ru" to WebExtension("Mega-mult", "mega-mult.ru",
        targets = "#movie_area, .vibor".split(","),
        css = """
            @target-0 iframe {
                width: 100vw;
            }
        """.trimIndent()),

    "online.animedia.tv" to WebExtension("Animedia", "online.animedia.tv",
        targets = "#player__jw, #season-search, #playlist__jw".split(","),
        css = """
            .main-page {min-width: 100vw;}
            @target-0 #player, #oframeplayer {width: 100vw;}
            @target-0 .post-player__controls {display: none;}
            @target-2 {
              display: block;
              position: absolute;
              left: 0;
              width: 100vw;
            }
        """.trimIndent()),

    "animevost.club" to WebExtension("Animevost", "animevost.club")
)

val pages = mutableListOf(
    WebPage("http://mega-mult.ru/serii/525-avatar-legenda-ob-aange-i-korre.html", extensionId = "71fbf02a-6b22-4b63-b26f-8e2178f67dc8"),
    WebPage("https://online.animedia.tv/anime/mastera-mecha-onlayn/1/1", extensionId = "b865f47a-ef2b-4ca5-83d8-e1f07a834f4e"),
    WebPage("http://animevost.club/online/legenda_o_grankreste_2018_720_hd/28-1-0-564", extensionId = "78a4e5e8-f2da-4255-9cae-0afe3bd63526")
)
