package com.nogipx.universalonlineplayer.deprecated.players

import org.w3c.dom.Document

interface VideoPlayer {
    val pageUrl: String

    /**
     * @return [Document] Player html from site.
     */
    fun getEmbedHtml() : String
}