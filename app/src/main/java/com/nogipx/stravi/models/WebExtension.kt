package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.annotations.Expose
import java.io.File


data class WebExtension(
    @Expose var name: String = "",
    @Expose var domain: String = "",
    @Expose var css: String = "",
    @Expose var targets: List<String> = listOf())
    : InternalStorage("extensions") {

    companion object {
        private const val TAG = "models.WebExtension"
    }

    fun save(context: Context) : File? =
        if (isNotEmpty())
            super.save(context, "extension_$domain", toJson().toByteArray())
        else null

    fun delete(context: Context) =
        super.delete(context,"extension_$domain")

    fun isEmpty() = name.isEmpty() && domain.isEmpty()

    fun isNotEmpty() = !isEmpty()
}