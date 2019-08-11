package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.Gson


data class WebExtension(
    var name: String = "",
    var id: String = "",
    var css: String = "",
    var targets: List<String> = listOf()) {

    companion object {
        const val INTERNAL_DIR = "extensions"

        fun fromJson(json: String?) : WebExtension =
            Gson().fromJson(json, WebExtension::class.java)

        fun get(context: Context, extensionId: String) : WebExtension? =
            getAll(context).find { it.id == extensionId }

        fun getAll(context: Context): List<WebExtension> =
            InternalManager(context)
                .allFromStorage(INTERNAL_DIR)!!
                .map { fromJson(it.readText()) }

        fun delete(context: Context, extensionId: String) =
            InternalManager(context)
                .deleteFile(INTERNAL_DIR, "extension_${fixId(extensionId)}")

        fun deleteAll(context: Context) =
            InternalManager(context)
                .deleteDir(INTERNAL_DIR)

        fun isFilenameFree(context: Context, filename: String) =
            InternalManager(context)
                .isFilenameFree(INTERNAL_DIR, filename)

        fun fixId(extensionId: String) = extensionId.replace(".", "-")

    }

    fun toJson(): String = Gson().toJson(this)

    fun toStorage(context: Context) =
        InternalManager(context).toStorage(
            INTERNAL_DIR,
            "extension_${fixId(id)}",
            toJson())

    fun isEmpty() = name.isEmpty() && id.isEmpty()

    fun isNotEmpty() = !isEmpty()

}