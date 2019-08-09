package com.nogipx.stravi.models

import com.google.gson.Gson


data class WebExtension(
    var name: String = "",
    var host: String = "",
    var css: String = "",
    var targets: List<String> = listOf()
) {
    companion object {
        fun fromJson(json: String?) : WebExtension =
            Gson().fromJson(json, WebExtension::class.java)
    }

    fun isEmpty() = name.isEmpty() && host.isEmpty()

    fun toJson(): String = Gson().toJson(this)
}