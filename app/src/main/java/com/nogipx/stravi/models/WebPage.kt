package com.nogipx.stravi.models

import java.net.URL


data class WebPage(
    var url: URL,
    var extension: WebExtension? = WebExtension(),
    var name: String = extension?.name ?: ""

) {
    fun isEmpty() = name.isEmpty()
}