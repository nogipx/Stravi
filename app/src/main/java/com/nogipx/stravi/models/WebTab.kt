package com.nogipx.stravi.models

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

    fun extensionsByHost(context: Context, host: String) : List<WebExtension> =
        WebExtension()
            .getAll<WebExtension>(context)
            .filter { it.host == host }

    fun attachedExtension(context: Context) : WebExtension? =
        WebExtension().get(context, extensionId)

    fun relatedExtensions(context: Context) : List<WebExtension> =
        extensionsByHost(context, URL(url).host)

    fun createExtension(context: Context) : WebExtension {
        val url = URL(url)
        val extension = WebExtension(host = url.host)
        extensionId = extension.uuid
        extension.save(context)
        return extension
    }
}
