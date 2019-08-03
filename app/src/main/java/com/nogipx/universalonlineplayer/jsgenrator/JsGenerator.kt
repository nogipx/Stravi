package com.nogipx.universalonlineplayer.jsgenrator

import org.jsoup.nodes.Element

/**
 * @author https://github.com/nogipx
 */
abstract class JsGenerator {

    companion object {
        const val GENCORE = "js-gen"
        const val CSS_GEN_UNIT = "$GENCORE-unit"
    }

    fun styleTag(text: String, attr: Map<String, String> = mapOf()) =
        createElement("style", attr).text(text.trimIndent())

    fun scriptTag(text: String, attr: Map<String, String> = mapOf()) =
        createElement("script", mapOf(
            "type" to "text/javascript"
        ) + attr).text(text.trimIndent())

    fun metaTag(attr: Map<String, String> = mapOf()) =
        createElement("meta", attr)

    fun createElement(tag: String, attributes: Map<String, String> = mapOf()) : Element {
        val element = Element(tag).addClass(CSS_GEN_UNIT)
        for ((k, v) in attributes) {
            when(v) {
                "true" -> element.attr(k, true)
                "false" -> element.attr(k, false)
                else -> element.attr(k, v)
            }
        }
        return element
    }

    open val generationChain: MutableList<FunctionJS> = mutableListOf()

    fun generate(chain: List<FunctionJS> = generationChain) : FunctionJS {
        var code = ""
        for (method in chain) {
            code += if (method.isNotEmpty()) "${method.iife()}\n" else ""
        }
        return FunctionJS(code = code)
    }


    /* Utilities */

    protected fun value(string: String, screen: Boolean = false) : String =
        if (screen) """\"$string\""""
        else "\"$string\""

    protected fun wrap(string: String, screen: Boolean = false) : String =
        if (screen) """\'$string\'"""
        else "'$string'"

    protected fun format(string: String) : String = "`$string `"

}


