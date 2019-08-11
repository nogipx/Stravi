package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.Gson
import java.net.URL


data class WebPage (
    var url: URL,
    var extensionId: String = url.host,
    var label: String = extensionId) {


    companion object {
        const val INTERNAL_DIR = "pages"

        fun fromJson(json: String): WebPage = Gson().fromJson(json, WebPage::class.java)

    }

    fun toJson(): String = Gson().toJson(this)

    fun allFromStorage(context: Context) =
        InternalManager(context).allFromStorage(INTERNAL_DIR)

    fun toStorage(context: Context) =
        InternalManager(context).toStorage(INTERNAL_DIR, "page_$label", toJson())

    fun isFilenameFree(context: Context, filename: String) =
        InternalManager(context).isFilenameFree(INTERNAL_DIR, filename)

    fun isEmpty() = label.isEmpty() || url.toString().isEmpty()

    fun isNotEmpty() = !isEmpty()
}