package com.nogipx.stravi.models

import android.content.Context
import android.util.Log
import com.google.gson.Gson


data class WebExtension(
    var name: String = "",
    var id: String = "",
    var css: String = "",
    var targets: List<String> = listOf()) {

    companion object {
        private const val TAG = "models.WebExtension"
        const val INTERNAL_DIR = "extensions"

        fun fromJson(json: String?) : WebExtension =
            Gson().fromJson(json, WebExtension::class.java)

        fun get(context: Context, extensionId: String) : WebExtension? {
            val extension = getAll(context).find { it.id == extensionId }
            return if (extension != null) {
                Log.d(TAG, "Got extension: ID='${extension.id}'")
                extension

            } else null
        }

        fun getAll(context: Context): List<WebExtension> =
            InternalManager(context)
                .dirFiles(INTERNAL_DIR)!!
                .map { fromJson(it.readText()) }

        fun deleteAll(context: Context) =
            InternalManager(context)
                .deleteDir(INTERNAL_DIR)

        fun isFilenameFree(context: Context, filename: String) =
            InternalManager(context)
                .isFilenameFree(INTERNAL_DIR, filename)

    }

    fun toJson(): String = Gson().toJson(this)

    fun save(context: Context) =
        InternalManager(context)
            .saveData(INTERNAL_DIR, "extension_$id", toJson().toByteArray())

    fun delete(context: Context) =
        InternalManager(context)
            .deleteFile(INTERNAL_DIR, "extension_$id")

    fun isEmpty() = name.isEmpty() && id.isEmpty()

    fun isNotEmpty() = !isEmpty()

}