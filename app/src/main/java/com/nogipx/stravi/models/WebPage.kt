package com.nogipx.stravi.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.net.URL


data class WebPage (
    var url: URL,
    var extensionId: String = url.host,
    var label: String = extensionId) {


    companion object {
        private const val TAG = "models.WebPage"
        const val INTERNAL_DIR = "pages"

        fun fromJson(json: String): WebPage =
            Gson().fromJson(json, WebPage::class.java)

        fun get(context: Context, extensionId: String) : WebExtension? {
            val extension = getAll(context).find { it.id == extensionId }
            return if (extension != null) {
                Log.d(TAG, "Got extension: ID='${extension.id}'")
                extension

            } else null
        }

        fun getAll(context: Context): List<WebExtension> =
            InternalManager(context)
                .dirFiles(WebExtension.INTERNAL_DIR)!!
                .map { WebExtension.fromJson(it.readText()) }

        fun deleteAll(context: Context) =
            InternalManager(context)
                .deleteDir(WebExtension.INTERNAL_DIR)

        fun isFilenameFree(context: Context, filename: String) =
            InternalManager(context)
                .isFilenameFree(WebExtension.INTERNAL_DIR, filename)

    }

    fun toJson(): String = Gson().toJson(this)

    fun getAll(context: Context) =
        InternalManager(context).dirFiles(INTERNAL_DIR)

    fun save(context: Context) =
        InternalManager(context).saveData(INTERNAL_DIR, "page_$label", toJson().toByteArray())

    fun delete(context: Context) =
        InternalManager(context).deleteFile(INTERNAL_DIR, "page_$label")

    fun isFilenameFree(context: Context, filename: String) =
        InternalManager(context).isFilenameFree(INTERNAL_DIR, filename)

    fun isEmpty() = label.isEmpty() || url.toString().isEmpty()

    fun isNotEmpty() = !isEmpty()
}