package com.nogipx.stravi.gateways.internal_storage.datatypes

import android.content.Context
import com.google.gson.annotations.Expose
import java.net.URL


data class WebTab (
    @Expose var url: String = "",
    @Expose var extensionId: String = "",
    @Expose internal var title: String = if (url.isNotEmpty()) URL(url).host else "")
    : InternalStorage("tabs") {

    override fun toString(): String = """
        ${className()}: label:$title, extensionId:$extensionId, url:$url
    """.trimIndent()

    fun isEmpty() = url.isEmpty() && title.isEmpty()

    fun isNotEmpty() = !isEmpty()

    fun attachedExtension(context: Context) : WebExtension? =
        WebExtension().get(context, extensionId)

//    fun createExtension(context: Context) : WebExtension {
//        return if (url.isNotEmpty()) {
//            val url = URL(url)
//            val extension = WebExtension(host = url.host)
//            extension.save(context)
//            extensionId = extension.uuid
//            extension
//
//        } else WebExtension()
//    }
}
