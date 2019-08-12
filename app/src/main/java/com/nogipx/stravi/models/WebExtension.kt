package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.annotations.Expose
import java.io.File


data class WebExtension(
    @Expose var name: String = "",
    @Expose var id: String = "",
    @Expose var css: String = "",
    @Expose var targets: List<String> = listOf())
    : InternalStorage("extensions") {

    companion object {
        private const val TAG = "models.WebExtension"
    }

    fun save(context: Context) : File? =
        if (isNotEmpty())
            super.save(context, "extension_$id", toJson().toByteArray())
        else null

    fun delete(context: Context) =
        super.delete(context,"extension_$id")

    fun isEmpty() = name.isEmpty() && id.isEmpty()

    fun isNotEmpty() = !isEmpty()
}