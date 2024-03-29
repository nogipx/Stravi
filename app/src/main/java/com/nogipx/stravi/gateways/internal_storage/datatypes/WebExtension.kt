package com.nogipx.stravi.gateways.internal_storage.datatypes

import android.content.Context
import android.util.Log
import com.google.gson.annotations.Expose
import com.nogipx.stravi.browser.settings.MyVisibilityJsGenerator
import com.nogipx.stravi.jsgenerator.FunctionJS


data class WebExtension(
    @Expose var active: Boolean = false,
    @Expose var name: String = "",
    @Expose var host: String = "",
    @Expose var css: String = "",
    @Expose var targets: List<String> = listOf())
    : InternalStorage("extensions") {


    fun generatePayload() : List<FunctionJS> {
        return if (active) {

            val generator = MyVisibilityJsGenerator(targets.toMutableList())
            if (css.isNotEmpty()) generator.addCSS(css)

            Log.e(TAG, "SELECTORS: ${generator.targetsSelector}")
            generator.generationChain

        } else listOf(FunctionJS())
    }

    fun extensionsByHost(context: Context, host: String) : List<WebExtension> =
        WebExtension()
            .getAll<WebExtension>(context)
            .filter { it.host == host }

    override fun toString(): String = """
        ${className()}: name:$name, host:$host, uuid:${uuid.take(8)})
    """.trimIndent()

    fun isEmpty() = host.isEmpty()

    fun isNotEmpty() = !isEmpty()
}