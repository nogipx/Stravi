package com.nogipx.stravi.models

import android.content.Context
import com.google.gson.annotations.Expose
import java.io.File
import java.net.URL


data class WebPage (
    @Expose var url: String = "",
    @Expose var extensionId: String = "",
    @Expose var label: String = if (url.isNotEmpty()) URL(url).host else "")
    : InternalStorage("pages") {

    companion object {
        private const val TAG = "models.WebPage"
    }

    fun save(context: Context) : File? =
        if (isNotEmpty())
            super.save(context, "page_$label", toJson().toByteArray())
        else null

    fun delete(context: Context) =
        super.delete(context, "page_$label")

    fun isEmpty() = url.isEmpty() && label.isEmpty()

    fun isNotEmpty() = !isEmpty()
}