package com.nogipx.stravi.gateways.internal_storage.datatypes

import com.google.gson.annotations.Expose
import java.net.URL


data class WebTab (
    @Expose var url: String = "",
    @Expose internal var title: String = if (url.isNotEmpty()) URL(url).host else "")
    : InternalStorage("tabs") {

    override fun toString(): String = """
        ${className()}: label:$title, url:$url
    """.trimIndent()

    fun isEmpty() = url.isEmpty() && title.isEmpty()

    fun isNotEmpty() = !isEmpty()
}
