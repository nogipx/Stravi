package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.annotations.Expose
import java.io.File


data class WebExtension(
    @Expose var name: String = "",
    @Expose var host: String = "",
    @Expose var css: String = "",
    @Expose var targets: List<String> = listOf())
    : InternalStorage("extensions") {

    companion object {
        private const val TAG = "models.WebExtension"
    }

    fun save(context: Context) : File? =
        if (isNotEmpty())
            super.save(context, "extension_$host", toJson().toByteArray())
        else null

    fun delete(context: Context) =
        super.delete(context,"extension_$host")

    fun isEmpty() = name.isEmpty() && host.isEmpty()

    fun isNotEmpty() = !isEmpty()
}