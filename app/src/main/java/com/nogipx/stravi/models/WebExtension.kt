package com.nogipx.stravi.models

import android.util.Log
import com.google.gson.annotations.Expose
import com.nogipx.stravi.browser.MyVisibilityJsGenerator
import com.nogipx.stravi.jsgenerator.FunctionJS


data class WebExtension(
    @Expose var active: Boolean = false,
    @Expose var name: String = "",
    @Expose var host: String = "",
    @Expose var css: String = "",
    @Expose var targets: List<String> = listOf())
    : InternalStorage("extensions") {


    fun generateJs() : List<FunctionJS> {
        return if (active) {

            val generator = MyVisibilityJsGenerator(targets.toMutableList())
            if (css.isNotEmpty()) generator.addCSS(css)

            Log.e(TAG, "SELECTORS: ${generator.targetsSelector}")
            generator.generationChain

        } else listOf(FunctionJS())
    }

    override fun toString(): String = """
        ${className()}: name:$name, host:$host, uuid:$uuid)
    """.trimIndent()

    fun isEmpty() = name.isEmpty() && host.isEmpty()

    fun isNotEmpty() = !isEmpty()
}